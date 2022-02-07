package com.payments.order.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.order.PaymentManagementApplication;
import com.payments.order.domain.Customer;
import com.payments.order.repository.CustomerRepository;
import com.payments.order.repository.OrderRepository;
import com.payments.order.service.dto.CustomerDTO;
import com.payments.order.service.mapper.CustomerMapper;



/**
 * Integration tests for the {@link CustomerResource} REST controller.
 */
@SpringBootTest(classes = PaymentManagementApplication.class)
@AutoConfigureMockMvc
public class CustomerResourceTests {
	
	
	   @Autowired
	   private MockMvc restCustomerMockMvc;
	   
	   @Autowired
	   private CustomerRepository customerRepository;
	   
	   @Autowired
	   private CustomerMapper customerMapper;
	   
	   private static final String CUSTOMER_NAME1 = "AAAAAAAAAA";
	   
	   private static final String CUSTOMER_NAME2 = "BBBBBBBBBB";
	   
	   private static final String ENTITY_API_URL = "/api/customers";
	   
	   private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
	   
	   private static final BigDecimal DEFAULT_WALLET = BigDecimal.ZERO;
	   
	   private static final BigDecimal CUSTOMER_BALANCE = BigDecimal.ZERO;
	   
	   private ObjectMapper mapper = new ObjectMapper();
	   
	    public static Customer createEntity() {
	        Customer customer = new Customer();
	        customer.setName(CUSTOMER_NAME1);
	        return customer;
	    }
	    
	    public static List<Customer> createEntityList() {
	    	List <Customer> customers=new ArrayList<Customer>();
	        Customer customer1 = new Customer();
	        customer1.setName(CUSTOMER_NAME1);
	        customer1.setWalletBalance(BigDecimal.ZERO);
	        customer1.setCustomerBalance(BigDecimal.ZERO);
	        
	        Customer customer2 = new Customer();
	        customer2.setName(CUSTOMER_NAME2);
	        customer2.setWalletBalance(BigDecimal.ZERO);
	        customer2.setCustomerBalance(BigDecimal.ZERO);
	        
	        customers.add(customer1);
	        customers.add(customer2);
	        
	        return customers;
	    }
	   
	    @Test
	    @Transactional
	    void createCustomer() throws Exception {
	        int databaseSizeBeforeCreate = customerRepository.findAll().size();
	        // Create the Customer
	        CustomerDTO customerDTO = customerMapper.toDto(createEntity());

	        restCustomerMockMvc
	            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(customerDTO)))
	            .andExpect(status().isCreated());
	        
	        
	        // Validate the Customer in the database
	        List<Customer> customerList = customerRepository.findAll();
	        assertThat(customerList).hasSize(databaseSizeBeforeCreate + 1);
	        Customer testCustomer = customerList.get(customerList.size() - 1);
	        assertThat(testCustomer.getName()).isEqualTo(CUSTOMER_NAME1);
	      
	    }
	    
	    @Test
	    @Transactional
	    void getAllCustomer() throws Exception {
	        // Initialize the database
	        customerRepository.saveAllAndFlush(createEntityList());

	        // Get the customer
	        restCustomerMockMvc
	            .perform(get(ENTITY_API_URL))
	            .andExpect(status().isOk())
	            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(jsonPath("$.[*].name").value(hasItem(CUSTOMER_NAME1)));
	    }
	    
	    @Test
	    @Transactional
	    void getCustomer() throws Exception {
	        // Initialize the database
	        Customer customer=customerRepository.saveAndFlush(createEntity());

	        // Get the customer
	        restCustomerMockMvc
	            .perform(get(ENTITY_API_URL_ID, customer.getId()))
	            .andExpect(status().isOk())
	            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
	            .andExpect(jsonPath("$.id").value(customer.getId().intValue()))
	            .andExpect(jsonPath("$.name").value(CUSTOMER_NAME1))
	            .andExpect(jsonPath("$.walletBalance").value(DEFAULT_WALLET))
	            .andExpect(jsonPath("$.customerBalance").value(CUSTOMER_BALANCE));
	    }
	    
	    @Test
	    @Transactional
	    void getNotExistCustomer() throws Exception {

	        restCustomerMockMvc
	            .perform(get(ENTITY_API_URL_ID, 1))
	            .andExpect(status().isNotFound());
	    }
	    
	    
	    @AfterAll
	    public static void cleanUp(@Autowired CustomerRepository customerRepository,@Autowired OrderRepository orderRepository){
	    	orderRepository.deleteAll();
	    	orderRepository.flush();
	    	
	    	customerRepository.deleteAll();
	    	customerRepository.flush();
	    }
	   

}
