package dlt;

import java.io.Serializable;

import dlt.common.Block;
import dlt.common.Utils;
import dlt.model.Payment;

public class PaymentBlock implements Block, Serializable {
	private static final long serialVersionUID = 1L;

	public final String blockNo;
	public final String previousHash;
	public final Payment payment;
	public String hash;

	public PaymentBlock(final String blockNo, final String previousHash, final Payment payment) {
		this.blockNo = blockNo;
		this.previousHash = previousHash;
		this.payment = payment;
		this.hash = calculateHash();
	}

	@Override
	public String getPreviousHash() {
		return previousHash;
	}

	@Override
	public Payment getPayment() {
		return payment;
	}

	@Override
	public String getHash() {
		return hash;
	}

	@Override
	public String calculateHash() {
		String calculatedhash = Utils.applySha256(previousHash + Long.toString(payment.ts) + payment);
		return calculatedhash;
	}

	@Override
	public String toString() {
		return "PaymentBlock [blockNo=" + blockNo + ", previousHash=" + previousHash + ", payment=" + payment
				+ ", hash=" + hash + "]";
	}
}
