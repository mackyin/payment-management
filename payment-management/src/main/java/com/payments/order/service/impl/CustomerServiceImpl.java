package com.payments.order.service.impl;


import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.payments.order.domain.Customer;
import com.payments.order.repository.CustomerRepository;
import com.payments.order.rest.errors.NoSuchElementException;
import com.payments.order.service.CustomerService;
import com.payments.order.service.dto.CustomerDTO;
import com.payments.order.service.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation for managing {@link Customer}.
 */
@Service
@Transactional
@Slf4j
public class CustomerServiceImpl implements CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CustomerMapper customerMapper;
		

	@Override
	public CustomerDTO save(CustomerDTO customerDTO) {
        log.debug("Request to save Customer : {}", customerDTO);
        Customer customer = customerMapper.toEntity(customerDTO);
        customer = customerRepository.save(customer);
        return customerMapper.toDto(customer);
	}

	@Override
	public Customer findById(Long id) {
		log.debug("Request to get Customer : {}", id);
		return customerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Customer not found id - "+id));
	}

	@Override
	public Customer save(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public List<Customer> findAll() {
		return customerRepository.findAll();
	}

	@Override
	public Optional<Customer> findOne(Long id) {
		return customerRepository.findById(id);
	}

	
}
