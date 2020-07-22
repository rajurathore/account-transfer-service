package com.raju.accounttransfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raju.accounttransfer.exception.TransactionException;
import com.raju.accounttransfer.model.Transaction;
import com.raju.accounttransfer.service.TransferService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/transaction-service")
@Api(value = "Transfer Service", description = "Defines the operations for transfering money from account A to B.")
public class TransferController {
	
	@Autowired
	private TransferService transferService;
	
	@PostMapping("/transfer")
	@ApiOperation(value = "Add the additional for the given employee.", response = Object.class)
	public ResponseEntity<Object> tranfer(@RequestBody Transaction transaction) {
		try {
			String status = transferService.transfer(transaction);
			return ResponseEntity.ok(status);
		} catch (TransactionException e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	
	
	

}
