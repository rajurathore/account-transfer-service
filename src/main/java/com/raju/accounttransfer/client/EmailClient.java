package com.raju.accounttransfer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.raju.accounttransfer.model.Email;

@FeignClient(name = "email-service")
public interface EmailClient {
	
	@PostMapping("/api/v1/send-email")
	public void email(@RequestBody Email email);

}
