package com.raju.accounttransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AccountTransferServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountTransferServiceApplication.class, args);
	}

}
