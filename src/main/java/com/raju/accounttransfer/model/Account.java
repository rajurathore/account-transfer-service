package com.raju.accounttransfer.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {

	@NotBlank(message = "Account number can not null or blank.")
	private String accountNumber;
	private BigDecimal balance;
	private Boolean isActive;
}
