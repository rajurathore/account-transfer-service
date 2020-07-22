package com.raju.accounttransfer.service;

import com.raju.accounttransfer.exception.TransactionException;
import com.raju.accounttransfer.model.Transaction;

public interface TransferService {
	
	/**
	 * @param transaction
	 * @return
	 * @throws TransactionException
	 */
	String transfer(Transaction transaction) throws TransactionException;

}
