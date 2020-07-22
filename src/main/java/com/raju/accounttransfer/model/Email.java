package com.raju.accounttransfer.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Email {
	
	private List<String> to, cc, bcc;
	private String subject;
	private String body;

}
