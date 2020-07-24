package com.raju.accounttransfer;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AccountTransferServiceApplication.class)
@ActiveProfiles({ "test" })
@SpringBootTest
class AccountTransferServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
