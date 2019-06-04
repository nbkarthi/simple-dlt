package dlt.msg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import dlt.Ledger;
import dlt.PaymentBlock;

@Singleton
public class DltBroadCaster extends ReceiverAdapter {
	JChannel channel;
	String user_name = System.getProperty("user.name", "n/a");
	final List<String> state = new LinkedList<String>();
	final Ledger ledger;
	@Inject
	public DltBroadCaster(final Ledger ledger) {
		
		this.ledger = ledger;
		try {
			channel = new JChannel();
			channel.setReceiver(this);
			channel.connect("dlt");
			channel.getState(null, 10000);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);
	}

	public void receive(Message msg) {
		Object object = msg.getObject();

		
		System.out.println("Received: "+object);
		if(object instanceof PaymentBlock){
			ledger.createBlock((PaymentBlock)object);
		}
	}

	public void getState(OutputStream output) throws Exception {
		synchronized (state) {
			Util.objectToStream(state, new DataOutputStream(output));
		}
	}

	@SuppressWarnings("unchecked")
	public void setState(InputStream input) throws Exception {
		List<String> list = (List<String>) Util.objectFromStream(new DataInputStream(input));
		synchronized (state) {
			state.clear();
			state.addAll(list);
		}
		System.out.println("received state (" + list.size() + " messages in chat history):");
		for (String str : list) {
			System.out.println(str);
		}
	}

	public void send(final PaymentBlock pb) {
		Message msg = new Message(null, pb);
		try {
			channel.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
