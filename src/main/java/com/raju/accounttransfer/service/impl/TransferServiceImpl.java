package com.raju.accounttransfer.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.raju.accounttransfer.client.EmailClient;
import com.raju.accounttransfer.exception.TransactionException;
import com.raju.accounttransfer.model.Account;
import com.raju.accounttransfer.model.Email;
import com.raju.accounttransfer.model.PersonalInfo;
import com.raju.accounttransfer.model.Transaction;
import com.raju.accounttransfer.repo.AccountRepository;
import com.raju.accounttransfer.service.TransferService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransferServiceImpl implements TransferService {

	@Autowired
	private AccountRepository repo;
	
	@Autowired
	private EmailClient client;

	@Override
	public String transfer(final Transaction transaction) throws TransactionException {
		checkSourceDestinationAccount(transaction);
		Account sourceAccount = this.repo.getAccountInfo(transaction.getSourceAccount());
		Account destinationAccount = this.repo.getAccountInfo(transaction.getDestinationAccount());
		checkAccounts(sourceAccount, destinationAccount, transaction);
		transfer(sourceAccount, destinationAccount, transaction);
		notifyCustomers(transaction);
		return "Success";
	}

	/**
	 * This is service will used to send email to the source and destination 
	 * account numbers to inform about the transaction in their account.
	 * @param transaction
	 */
	private void notifyCustomers(final Transaction transaction) {

		PersonalInfo fromAcc = this.repo.getPersonalInfo(transaction.getSourceAccount());
		PersonalInfo toAcc = this.repo.getPersonalInfo(transaction.getDestinationAccount());

		this.client.email(new Email(Arrays.asList(fromAcc.getEmail()), null, null, "Amount transfer..!",
				String.format("Amount %s debited from your account", transaction.getAmount())));
		this.client.email(new Email(Arrays.asList(toAcc.getEmail()), null, null, "Amount transfer..!",
				String.format("Amount %s credited in your account", transaction.getAmount())));
	}

	/**
	 * @param sourceAccount
	 * @param destinationAccount
	 * @param transaction
	 */
	private void transfer(Account sourceAccount, Account destinationAccount, Transaction transaction) {
		BigDecimal updatedSourceBalance = sourceAccount.getBalance().subtract(transaction.getAmount());
		BigDecimal updatedDestBalance = destinationAccount.getBalance().add(transaction.getAmount());
		sourceAccount.setBalance(updatedSourceBalance);
		destinationAccount.setBalance(updatedDestBalance);
		this.repo.updateAccountInfo(sourceAccount.getAccountNumber(), sourceAccount);
		this.repo.updateAccountInfo(destinationAccount.getAccountNumber(), destinationAccount);
	}

	/**
	 * @param sourceAccount
	 * @param destinationAccount
	 * @param transaction
	 * @throws TransactionException
	 */
	private void checkAccounts(final Account sourceAccount, final Account destinationAccount,
			final Transaction transaction) throws TransactionException {
		if (transaction.getAmount().compareTo(sourceAccount.getBalance()) == 1) {
			log.error("Transfer amount can not be greater than available balance.");
			throw new TransactionException("Transfer amount can not be greater than available balance.");
		} else if(!sourceAccount.getIsActive()) {
			log.error(String.format("Account %s is not active.", sourceAccount.getAccountNumber()));
			throw new TransactionException(String.format("Account %s is not active.", sourceAccount.getAccountNumber()));
		} else if(!destinationAccount.getIsActive()) {
			log.error(String.format("Account %s is not active.", destinationAccount.getAccountNumber()));
			throw new TransactionException(String.format("Account %s is not active.", destinationAccount.getAccountNumber()));
		}
	}

	/**
	 * @param transaction
	 * @throws TransactionException
	 */
	private void checkSourceDestinationAccount(final Transaction transaction) throws TransactionException {
		if (transaction.getSourceAccount().equalsIgnoreCase(transaction.getDestinationAccount())) {
			throw new TransactionException("Both source and destination account can not be same.");
		}
	}

}
