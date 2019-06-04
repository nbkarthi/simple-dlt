package dlt.model;

import java.io.Serializable;

public class Payment implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public String txnId;
	public String accountNumber;
	public String bankName;
	public String currency;
	public String amount;
	public long ts;

	public Payment(String accountNo, String bankName, String currency, String amount, long ts) {
		this.accountNumber = accountNo;
		this.bankName = bankName;
		this.currency = currency;
		this.amount = amount;
		this.ts = ts;
	}
	
	@Override
	public String toString() {
		return "Payment [accountNumber=" + accountNumber + ", bankName=" + bankName + ", currency=" + currency
				+ ", amount=" + amount + ", ts=" + ts + "]";
	}

	public PaymentModel model() {
		return new PaymentModel(txnId, accountNumber, bankName, currency, amount, ts);
	}

	public void with(String uid, long now) {
		this.txnId = uid;
		this.ts = now;
	}
}
