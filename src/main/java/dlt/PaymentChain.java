package dlt;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import dlt.common.Utils;
import dlt.model.Payment;
import dlt.msg.DltBroadCaster;

public class PaymentChain {

	private String lastHash;
	private final Ledger ledger;
	private final DltBroadCaster blockPersister;
	private static final Logger log = LoggerFactory.getLogger(PaymentChain.class);

	@Inject
	PaymentChain(final Ledger ledger, final DltBroadCaster blockPersister) {
		this.ledger = ledger;
		this.blockPersister = blockPersister;
	}

	public static void main(String[] args) {

		final Injector injector = Guice.createInjector();
		final PaymentChain app = injector.getInstance(PaymentChain.class);
		app.start();
	}

	private void start() {
		lastHash = ledger.lastHash();
		configureServer();
	}

	private void configureServer() {
		port(Integer.parseInt(System.getProperty("port")));
		staticFiles.location("/public");

		post("/new-payment", (req, res) -> {
			newPayment(req.body());
			return "ok";
		});

		get("/poll-payments", (req, res) -> ledger.blocks());
	}

	private void newPayment(final String reqBody) {
		log.info("New Payment Posted: {}", reqBody);
		final Payment pmt = Utils.fromJSON(reqBody, Payment.class);
		final long now = Utils.now();
		pmt.with(Utils.uid(), now);
		final PaymentBlock pb = new PaymentBlock(String.valueOf(now), lastHash, pmt);
		lastHash = pb.getHash();
		blockPersister.send(pb);
	}
}
