package dlt.model;

public class PaymentModel {
	public String blockNo;
	public String txnId;
	public String accountNumber;
	public String bankName;
	public String currency;
	public String amount;
	public long ts;
	public int status=0;

	public PaymentModel(String txnId, String accountNo, String bankName, String currency, String amount, long ts) {
		this.txnId = txnId;
		this.accountNumber = accountNo;
		this.bankName = bankName;
		this.currency = currency;
		this.amount = amount;
		this.ts = ts;
	}
	
	public PaymentModel withBlockDetails(final String blockNo, final int status){
		this.blockNo = blockNo;
		this.status = status;
		return this;
	}
}
