package com.raju.accounttransfer.repo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.raju.accounttransfer.model.Account;
import com.raju.accounttransfer.model.PersonalInfo;

@Repository
public class AccountRepository {
	
	private static final Map<String, PersonalInfo> PERSON_MAP = new HashMap<>();
	private static final Map<String, Account> ACCOUNT_MAP = new HashMap<>();
	
	static {
		PERSON_MAP.put("100", new PersonalInfo("Cust01", "Cust01@test.com"));
		PERSON_MAP.put("101", new PersonalInfo("Cust02", "Cust02@test.com"));
		PERSON_MAP.put("102", new PersonalInfo("Cust03", "Cust03@test.com"));
		ACCOUNT_MAP.put("100", new Account("100", new BigDecimal(10000), true));
		ACCOUNT_MAP.put("101", new Account("101", new BigDecimal(5000), true));
		ACCOUNT_MAP.put("102", new Account("102", new BigDecimal(8000), false));
	}
	
	/**
	 * @param accountNumber
	 * @return
	 */
	public PersonalInfo getPersonalInfo(final String accountNumber) {
		return PERSON_MAP.get(accountNumber);
	}

	/**
	 * @param accountNumber
	 * @return
	 */
	public Account getAccountInfo(final String accountNumber) {
		return ACCOUNT_MAP.get(accountNumber);
	}
	
	/**
	 * @param accountNumber
	 * @param account
	 */
	public void updateAccountInfo(final String accountNumber, final Account account) {
		ACCOUNT_MAP.put(accountNumber, account);
	}
	
}
