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
import com.payments.order.domain.enumeration.OrderStatus;
import com.payments.order.domain.enumeration.PaymentStatus;
import com.payments.order.repository.CustomerRepository;
import com.payments.order.repository.OrderRepository;
import com.payments.order.service.OrderService;
import com.payments.order.service.dto.OrderDTO;


/**
 * Integration tests for the {@link OrderResource} REST controller.
 */
@SpringBootTest(classes = PaymentManagementApplication.class)
@AutoConfigureMockMvc
public class OrderResourceTests {

	@Autowired
	private MockMvc restOrderMockMvc;
	
	@Autowired
	private OrderRepository orderRepository;	
	
	@Autowired
	private OrderService orderService;
	
	private static Customer customer=null;
	
    private static final String ENTITY_API_URL = "/api/orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_CUSTOMER_ID = ENTITY_API_URL +"/customer"+ "/{customerId}";
    private static final BigDecimal DEFAULT_BILL_AMOUNT=new BigDecimal(100);
    private static final OrderStatus DEFAULT_ORDER_STATUS=OrderStatus.PENDING;
    private static final PaymentStatus DEFAULT_PAYMENT_STATUS=PaymentStatus.UNDER_PAID;
    private ObjectMapper objMapper = new ObjectMapper();

    @BeforeAll
    public static void setup(@Autowired CustomerRepository customerRepository) {
		customer=customerRepository.saveAndFlush(CustomerResourceTests.createEntity());
	}


	public static Order createEntity() {
		Order order = new Order();
		order.setBillAmount(DEFAULT_BILL_AMOUNT);
		order.setCustomer(customer);
		order.setOrderStatus(DEFAULT_ORDER_STATUS);
		order.setPaymentStatus(DEFAULT_PAYMENT_STATUS);
		return order;
	}
	
	public static OrderDTO createEntityDTO() {
		OrderDTO orderDto = new OrderDTO();
		orderDto.setBillAmount(new BigDecimal(100));
		orderDto.setCustomerId(customer.getId());

		return orderDto;
	}
	
    @Test
    @Transactional
    void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objMapper.writeValueAsBytes(createEntityDTO())))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getBillAmount()).isEqualByComparingTo(DEFAULT_BILL_AMOUNT);
        assertThat(testOrder.getOrderStatus()).isEqualTo(DEFAULT_ORDER_STATUS);
    }
    
    @Test
    @Transactional
    void createOrderForNotExistingCustomer() throws Exception {
        OrderDTO orderDTO=createEntityDTO();
        orderDTO.setCustomerId(Long.valueOf(5));

        restOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(objMapper.writeValueAsBytes(orderDTO)))
            .andExpect(status().is4xxClientError());

    }
    
    @Test
    @Transactional
    void getAllOrders() throws Exception {
        Order order=orderService.save(createEntity());
    	  	
        // Get all the orderList
        restOrderMockMvc
            .perform(get(ENTITY_API_URL))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].billAmount").value(hasItem(new NumberMatcher(DEFAULT_BILL_AMOUNT))))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())));
    }
    
    @Test
    @Transactional
    void getOrder() throws Exception {
        // Initialize the database
    	Order order=orderService.save(createEntity());

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.billAmount").value(new NumberMatcher(DEFAULT_BILL_AMOUNT)))
            .andExpect(jsonPath("$.orderStatus").value(DEFAULT_ORDER_STATUS.toString()));
    }
    
    @Test
    @Transactional
    void getNotExistingOrder() throws Exception {

        // Get the order
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, 1))
            .andExpect(status().is4xxClientError());

    }
    
    @Test
    @Transactional
    void getAllOrderByCustomerId() throws Exception {
        // Initialize the database
    	Order order=orderService.save(createEntity());

        // Get the order by customer id
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_CUSTOMER_ID, customer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].billAmount").value(hasItem(new NumberMatcher(DEFAULT_BILL_AMOUNT))))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())));
    }   
 
    @Test
    @Transactional
    void getAllOrderByNotExistingCustomerId() throws Exception {//To DO
        // Initialize the database
    	Order order=orderService.save(createEntity());

        // Get the order by customer id
        restOrderMockMvc
            .perform(get(ENTITY_API_URL_CUSTOMER_ID, 2))
            .andExpect(status().is4xxClientError());
    } 

    
    @AfterAll
    public static void cleanUp(@Autowired CustomerRepository customerRepository,@Autowired OrderRepository orderRepository){
    	orderRepository.deleteAll();
    	orderRepository.flush();
    	
    	customerRepository.deleteAll();
    	customerRepository.flush();
    	

    }
    
    
}
