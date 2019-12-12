package org.tshark.tsz.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tshark.tsz.rabbitmq.customer.GerritEventCustomer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class RabbitmqCustomerApplication {

	private static List<GerritEventCustomer> gerritEventCustomerList = new ArrayList<>();

	public static void main(String[] args) throws IOException, TimeoutException {
		SpringApplication.run(RabbitmqCustomerApplication.class, args);
		for (int i = 0; i < 10; i++) {
			gerritEventCustomerList.add(new GerritEventCustomer(""+i));
		}

	}
}
