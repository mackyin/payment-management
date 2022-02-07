package com.payments.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan
public class PaymentManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaymentManagementApplication.class, args);
	}

}
