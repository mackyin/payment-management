package com.payments.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.payments.order.domain.Order;
import com.payments.order.domain.enumeration.OrderStatus;
import com.payments.order.service.dto.OrderDTO;

/**
 * Service Interface for managing {@link com.payments.order.domain.Order}.
 */
public interface OrderService {
    
	/**
     * Save a order.
     *
     * @param orderDTO the entity to save.
     * @return the persisted entity.
     */
    OrderDTO save(OrderDTO orderDTO);
    
    /**
     * Save a order.
     *
     * @param order the entity to save.
     * @return the persisted entity.
     */
    Order save(Order order);
       
    /**
     * calculate net balance to pay by customer
     *
     * @param Long customerId,OrderStatus orderStatus,BigDecimal unProcessedNetBalance
     * @return return calculated value
     */
    BigDecimal calculateNetBalanceByCustomerId(Long customerId,OrderStatus orderStatus,BigDecimal unProcessedNetBalance);

    /**
     * Get the "id" Order.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Order findById(Long id);
    
    /**
     * Get all the orders.
     *
     * @return the list of entities.
     */
    List<Order> findAll();

    /**
     * Get the "id" order.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Order> findOne(Long id);
    
    /**
     * Get the "customerId" customer.
     *
     * @param customerId the customerId of the entity customer.
     * @return the order by matching customer id.
     */
    List<Order> getOrderByCustomerId(Long customerId);

}
