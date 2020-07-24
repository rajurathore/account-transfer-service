package com.raju.accounttransfer.controller;

import org.junit.runner.RunWith;

import org.junit.FixMethodOrder;
//import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raju.accounttransfer.client.EmailClient;
import com.raju.accounttransfer.exception.TransactionException;
import com.raju.accounttransfer.model.Account;
import com.raju.accounttransfer.model.Email;
import com.raju.accounttransfer.model.Transaction;
import com.raju.accounttransfer.repo.AccountRepository;
import com.raju.accounttransfer.service.TransferService;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ "test" })
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransferControllerTest {

	private static final String TRANSACTION_ENDPOINT = "/api/v1/transaction-service";

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private TransferService transferService;

	@MockBean
	private AccountRepository repo;

	@MockBean
	private EmailClient client;

	@BeforeEach
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testTransfer() throws Exception {
		String request = mapper.writeValueAsString(new Transaction());
		Mockito.when(transferService.transfer(Mockito.any(Transaction.class))).thenReturn("Success");
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(TRANSACTION_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.body").value("Success"));
	}

	@Test
	public void testTransferWithException() throws Exception {
		String request = mapper.writeValueAsString(new Transaction());
		Mockito.when(transferService.transfer(Mockito.any(Transaction.class)))
				.thenThrow(new TransactionException("Failed"));
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(TRANSACTION_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.body").value("Failed"));
	}

	@Test
	public void testWhenSourceAndDestinationAccountSame() throws Exception {
		Transaction tranx = new Transaction();
		tranx.setSourceAccount("100");
		tranx.setDestinationAccount("100");
		tranx.setAmount(new BigDecimal(100));
		String request = mapper.writeValueAsString(tranx);
		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(TRANSACTION_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.body").value("Both source and destination account can not be same."));
	}

	@Test
	public void testWhenTransferAmountIsMoreThanAvailableBalance() throws Exception {
		Transaction tranx = new Transaction();
		tranx.setSourceAccount("100");
		tranx.setDestinationAccount("101");
		tranx.setAmount(new BigDecimal(100));
		String request = mapper.writeValueAsString(tranx);
		Account sourceAcc = new Account("100", BigDecimal.valueOf(50), true);
		Account destinationAcc = new Account("101", BigDecimal.valueOf(100), true);

		Mockito.when(this.repo.getAccountInfo(Mockito.anyString())).thenReturn(sourceAcc);
		Mockito.when(this.repo.getAccountInfo(Mockito.anyString())).thenReturn(destinationAcc);

		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(TRANSACTION_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.body").value("Transfer amount can not be greater than available balance."));
	}

	@Test
	public void testTransferWhenSourceIsNotActive() throws Exception {
		Transaction tranx = new Transaction();
		tranx.setSourceAccount("100");
		tranx.setDestinationAccount("101");
		tranx.setAmount(new BigDecimal(100));
		String request = mapper.writeValueAsString(tranx);
		Account sourceAcc = new Account("100", BigDecimal.valueOf(150), false);
		Account destinationAcc = new Account("101", BigDecimal.valueOf(100), true);

		Mockito.when(this.repo.getAccountInfo(Mockito.anyString())).thenReturn(sourceAcc);
		Mockito.when(this.repo.getAccountInfo(Mockito.anyString())).thenReturn(destinationAcc);

		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(TRANSACTION_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andExpect(
				jsonPath("$.body").value(String.format("Account %s is not active.", sourceAcc.getAccountNumber())));
	}

	@Test
	public void testTransferWhenDestinationIsNotActive() throws Exception {
		Transaction tranx = new Transaction();
		tranx.setSourceAccount("100");
		tranx.setDestinationAccount("101");
		tranx.setAmount(new BigDecimal(100));
		String request = mapper.writeValueAsString(tranx);
		Account sourceAcc = new Account("100", BigDecimal.valueOf(150), true);
		Account destinationAcc = new Account("101", BigDecimal.valueOf(100), false);

		Mockito.when(this.repo.getAccountInfo(Mockito.anyString())).thenReturn(sourceAcc);
		Mockito.when(this.repo.getAccountInfo(Mockito.anyString())).thenReturn(destinationAcc);

		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(TRANSACTION_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andExpect(jsonPath("$.body")
				.value(String.format("Account %s is not active.", destinationAcc.getAccountNumber())));
	}

	@Test
	public void testTransferSuccessfully() throws Exception {
		Transaction tranx = new Transaction();
		tranx.setSourceAccount("100");
		tranx.setDestinationAccount("101");
		tranx.setAmount(new BigDecimal(100));
		String request = mapper.writeValueAsString(tranx);
		Account sourceAcc = new Account("100", BigDecimal.valueOf(150), true);
		Account destinationAcc = new Account("101", BigDecimal.valueOf(100), true);

		Mockito.when(this.repo.getAccountInfo(Mockito.anyString())).thenReturn(sourceAcc);
		Mockito.when(this.repo.getAccountInfo(Mockito.anyString())).thenReturn(destinationAcc);

		Mockito.doNothing().when(repo).updateAccountInfo(Mockito.anyString(), Mockito.any(Account.class));

		Mockito.doNothing().when(client).email(Mockito.any(Email.class));

		final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(TRANSACTION_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(jsonPath("$.body").value("Success"));
	}

}
