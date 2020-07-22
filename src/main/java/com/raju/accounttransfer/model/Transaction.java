package com.raju.accounttransfer.model;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Transaction {

	@NotBlank(message = "Source account number can not be null or blank.")
	private String sourceAccount;
	@NotBlank(message = "Destination account number can not be null or blank.")
	private String destinationAccount;
	@NotNull
	@Min(value = 0)
	private BigDecimal amount;
}
