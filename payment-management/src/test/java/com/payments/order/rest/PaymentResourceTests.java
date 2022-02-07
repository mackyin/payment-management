package com.payments.order.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.order.PaymentManagementApplication;
import com.payments.order.Util.NumberMatcher;
import com.payments.order.domain.Customer;
import com.payments.order.domain.Order;
import com.payments.order.domain.Payment;
import com.payments.order.domain.enumeration.OrderStatus;
import com.payments.order.domain.enumeration.PaymentMethod;
import com.payments.order.domain.enumeration.PaymentStatus;
import com.payments.order.repository.CustomerRepository;
import com.payments.order.repository.OrderRepository;
import com.payments.order.repository.PaymentRepository;
import com.payments.order.service.dto.PaymentDTO;

/**
 * Integration tests for the {@link PaymentResourceTests} REST controller.
 */
@SpringBootTest(classes = PaymentManagementApplication.class)
@AutoConfigureMockMvc
public class PaymentResourceTests {

	private static final BigDecimal DEFAULT_PAID_AMOUNT = new BigDecimal(110);
	private static final BigDecimal DEFAULT_BILL_AMOUNT = new BigDecimal(100);
	private static final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.CREDIT_CARD;
	private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.UNDER_PAID;
	private static final OrderStatus DEFAULT_ORDER_STATUS = OrderStatus.PENDING;
	private static final String DEFAULT_NAME = "AAAAAAAAAA";
	private static final String ENTITY_API_URL = "/api/payments";
	private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

	@Autowired
	private MockMvc restPaymentMockMvc;

	@Autowired
	private PaymentRepository paymentRepository;

	private ObjectMapper mapper = new ObjectMapper();

	private Payment payment;

	private static Order order;

	@BeforeAll
	public static void setup(@Autowired CustomerRepository customerRepository,
			@Autowired OrderRepository orderRepository) {
		Customer customer = new Customer();
		customer.setName(DEFAULT_NAME);

		customer = customerRepository.saveAndFlush(customer);

		order = new Order();
		order.setBillAmount(DEFAULT_BILL_AMOUNT);
		order.setNetBalance(DEFAULT_BILL_AMOUNT.negate());
		order.setOrderStatus(DEFAULT_ORDER_STATUS);
		order.setPaymentStatus(DEFAULT_PAYMENT_STATUS);
		order.setCustomer(customer);
		orderRepository.saveAndFlush(order);

	}

	public static PaymentDTO createEntityDto() {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setOrderId(order.getId());
		paymentDTO.setPaymentMethod(DEFAULT_PAYMENT_METHOD);
		paymentDTO.setPaidAmount(DEFAULT_PAID_AMOUNT);
		paymentDTO.setUseWalletBalance(false);
		return paymentDTO;
	}

	public static Payment createEntity() {
		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setPaymentMethod(DEFAULT_PAYMENT_METHOD);
		payment.setPaidAmount(DEFAULT_PAID_AMOUNT);
		return payment;
	}

	@Test
	@Transactional
	void createPayment() throws Exception {

		int databaseSizeBeforeCreate = paymentRepository.findAll().size();
		// Create the Payment
		restPaymentMockMvc.perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(createEntityDto()))).andExpect(status().isCreated());

		// Validate the Payment in the database
		List<Payment> paymentList = paymentRepository.findAll();
		assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
		Payment testPayment = paymentList.get(paymentList.size() - 1);
		assertThat(testPayment.getPaidAmount()).isEqualByComparingTo(DEFAULT_PAID_AMOUNT);
		assertThat(testPayment.getPaymentMethod()).isEqualTo(DEFAULT_PAYMENT_METHOD);

	}

	@Test
	@Transactional
	void getAllPayments() throws Exception {
		// Initialize the database
		payment = paymentRepository.saveAndFlush(createEntity());

		// Get all the paymentList
		restPaymentMockMvc.perform(get(ENTITY_API_URL)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
				.andExpect(jsonPath("$.[*].paidAmount").value(hasItem(new NumberMatcher(DEFAULT_PAID_AMOUNT))))
				.andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())));
	}

	@Test
	@Transactional
	void getPaymentById() throws Exception {
		// Initialize the database
		payment = paymentRepository.saveAndFlush(createEntity());

		restPaymentMockMvc.perform(get(ENTITY_API_URL_ID, payment.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.id").value(payment.getId().intValue()))
				.andExpect(jsonPath("$.paidAmount").value(new NumberMatcher(DEFAULT_PAID_AMOUNT)))
				.andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD.toString()));
	}

	@Test
	@Transactional
	void getPaymentByNotExistId() throws Exception {
		// Initialize the database
		payment = paymentRepository.saveAndFlush(createEntity());

		restPaymentMockMvc.perform(get(ENTITY_API_URL_ID, 3)).andExpect(status().is4xxClientError());
	}

	@AfterAll
	public static void cleanUp(@Autowired CustomerRepository customerRepository,
			@Autowired OrderRepository orderRepository) {
		orderRepository.deleteAll();
		orderRepository.flush();

		customerRepository.deleteAll();
		customerRepository.flush();
	}

}
