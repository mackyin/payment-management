package com.payments.order.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.payments.order.domain.Customer;
import com.payments.order.domain.Order;
import com.payments.order.domain.enumeration.OrderStatus;
import com.payments.order.repository.CustomerRepository;
import com.payments.order.repository.OrderRepository;
import com.payments.order.rest.errors.NoSuchElementException;
import com.payments.order.service.CustomerService;
import com.payments.order.service.OrderService;
import com.payments.order.service.dto.OrderDTO;
import com.payments.order.service.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService{

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private CustomerService customerService;

	@Override
	public OrderDTO save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        
        Order order = orderMapper.convertDtoToEntity(orderDTO);
        order = orderRepository.save(updateCustomerObject(order));
        
        return orderMapper.entityToDto(order);
	}
	
    public Order updateCustomerObject(Order order) {
    	order.setCustomer(updateCustomerBalance(order));
    	return order;    	
    }
    
	public Customer updateCustomerBalance(Order order) {
		log.debug("Update customer net balance & wallet details for order : {}", order);
		Customer customer = customerService.findById(order.getCustomer().getId());
		
		//wallet balance always positive value & net balance with pending status always negative,difference between both will return cumulative net balance for customer 
		customer.setCustomerBalance(customer.getWalletBalance().subtract(
				(calculateNetBalanceByCustomerId(customer.getId(), OrderStatus.PENDING, order.getNetBalance())).abs())); 
		return customer;
	}

	@Override
	public Order save(Order order) {
		log.debug("Request to save Order : {}", order);
		return orderRepository.save(order);
	}

	@Override
	public Order findById(Long id) {
		log.debug("Request to get Order : {}", id);
		return orderRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Order not found id - "+id));
	}

	//Retrieve existing net balance for this customer & add new order net balance,it will return cumulative net balance
	@Override
	public BigDecimal calculateNetBalanceByCustomerId(Long customerId,OrderStatus orderStatus,BigDecimal unProcessedNetBalance) {
		log.debug("Request to get cumulative net balance for customer id {}",customerId);
		BigDecimal currentNetBalance=orderRepository.calculateNetBalanceByCustomerId(customerId,orderStatus);
		return currentNetBalance!=null?unProcessedNetBalance.add(currentNetBalance):unProcessedNetBalance;
	}

	@Override
	public List<Order> findAll() {
		return orderRepository.findAll();
	}

	@Override
	public Optional<Order> findOne(Long id) {
		return orderRepository.findById(id);
	}

	@Override
	public List<Order> getOrderByCustomerId(Long customerId) {
		return orderRepository.getOrderByCustomerId(customerId);
	}
	
		
}
