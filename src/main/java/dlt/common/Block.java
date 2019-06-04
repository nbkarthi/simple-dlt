package dlt.common;

import dlt.model.Payment;

public interface Block {

	String getPreviousHash();

	Payment getPayment();

	String getHash();

	String calculateHash();

}