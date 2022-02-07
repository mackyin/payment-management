package com.payments.order.service;


import java.util.List;
import java.util.Optional;
import com.payments.order.domain.Customer;
import com.payments.order.service.dto.CustomerDTO;

/**
 * Service Interface for managing {@link com.payments.order.domain.Customer}.
 */
public interface CustomerService {
    /**
     * Save a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    CustomerDTO save(CustomerDTO customerDTO);
    
    /**
     * Save a customer.
     *
     * @param customer the entity to save.
     * @return the persisted entity.
     */   
    Customer save(Customer customer);
    
    /**
     * Get the "id" customer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Customer findById(Long id);
    
    /**
     * Get all the customers.
     *
     * @return the list of entities.
     */
    List<Customer> findAll();

    /**
     * Get the "id" customer.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Customer> findOne(Long id);


}
