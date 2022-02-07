package com.payments.order.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.order.PaymentManagementApplication;
import com.payments.order.domain.Customer;
import com.payments.order.domain.Order;
import com.payments.order.domain.enumeration.PaymentMethod;
import com.payments.order.repository.CustomerRepository;
import com.payments.order.repository.OrderRepository;
import com.payments.order.service.dto.CustomerDTO;
import com.payments.order.service.dto.OrderDTO;
import com.payments.order.service.dto.PaymentDTO;

/**
 * Integration tests for validating business flow as per requirement.
 */
@SpringBootTest(classes = PaymentManagementApplication.class)
@AutoConfigureMockMvc
public class ValidateBusinessFlowTest {

	@Autowired
	private MockMvc restMockMvc;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrderRepository orderRepository;

	private ObjectMapper mapper = new ObjectMapper();

	private static final String CUSTOMER_NAME1 = "AAAAAAAAAA";
	private static final String CUSTOMER_API_URL = "/api/customers";
	private static final String ORDER_API_URL = "/api/orders";
	private static final String PAYMENT_API_URL = "/api/payments";

	public CustomerDTO createCustomerDto() {
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setName(CUSTOMER_NAME1);
		return customerDTO;
	}

	public OrderDTO createOrderDto1() {//Order one with bill 100
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setBillAmount(new BigDecimal(100));
		orderDTO.setCustomerId(1L);

		return orderDTO;
	}
	
	public OrderDTO createOrderDto2() {//order two with bill 50
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setBillAmount(new BigDecimal(100));
		orderDTO.setCustomerId(1L);

		return orderDTO;
	}

	public PaymentDTO createPaymentDto() {//Payment was made 110 for order1,paid 10 over paid 
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setOrderId(1L);
		paymentDTO.setPaidAmount(new BigDecimal(110));
		paymentDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		paymentDTO.setUseWalletBalance(false);
		return paymentDTO;

	}

	@Test
	@Transactional
	void createCustomer() throws Exception {

		// Create the Customer
		MvcResult customerResult = restMockMvc
				.perform(post(CUSTOMER_API_URL).contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsBytes(createCustomerDto())))
				.andExpect(status().isCreated()).andReturn();

		String customerJson = customerResult.getResponse().getContentAsString();
		CustomerDTO customerDTO = mapper.readValue(customerJson, CustomerDTO.class);

		// Create the order
		OrderDTO requestOrderDto1 = createOrderDto1();
		requestOrderDto1.setCustomerId(customerDTO.getId());

		MvcResult orderResult = restMockMvc
				.perform(post(ORDER_API_URL).contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsBytes(requestOrderDto1)))
				.andExpect(status().isCreated()).andReturn();

		String orderJson1 = orderResult.getResponse().getContentAsString();
		OrderDTO orderDTO = mapper.readValue(orderJson1, OrderDTO.class);

		// Create payment
		PaymentDTO requestPaymentDto1 = createPaymentDto();
		requestPaymentDto1.setOrderId(orderDTO.getId());

		restMockMvc.perform(post(PAYMENT_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(requestPaymentDto1))).andExpect(status().isCreated());

		//verify the customer balance 
		Customer customer = customerRepository.getById(customerDTO.getId());
		assertThat(customer.getName()).isEqualTo(CUSTOMER_NAME1);
		assertThat(customer.getCustomerBalance().compareTo(new BigDecimal(10)) == 0);
		assertThat(customer.getWalletBalance().compareTo(new BigDecimal(10)) == 0);
		
		//creating order 2 for amount of 50
		OrderDTO requestOrderDto2 = createOrderDto1();
		requestOrderDto1.setCustomerId(customerDTO.getId());

		MvcResult orderResult2 = restMockMvc
				.perform(post(ORDER_API_URL).contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsBytes(requestOrderDto1)))
				.andExpect(status().isCreated()).andReturn();

		String orderJson2 = orderResult2.getResponse().getContentAsString();
		OrderDTO orderDTO2 = mapper.readValue(orderJson2, OrderDTO.class);
		
		//Validate order balance,it should be 50 to pay
		
		Order order=orderRepository.findById(orderDTO2.getId()).orElseThrow();
		assertThat(order.getNetBalance().compareTo(new BigDecimal(-50)) == 0);
		
		//Validate customer balance ,it should be -40
		
		Customer customerAfterSecondOrder=customerRepository.getById(order.getCustomer().getId());		
		assertThat(customerAfterSecondOrder.getCustomerBalance().compareTo(new BigDecimal(-40)) == 0);
		assertThat(customerAfterSecondOrder.getWalletBalance().compareTo(new BigDecimal(10)) == 0);
	}

}
