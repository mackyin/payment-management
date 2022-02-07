package com.payments.order.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.payments.order.domain.Order;
import com.payments.order.rest.errors.BadRequestException;
import com.payments.order.rest.errors.NoSuchElementException;
import com.payments.order.rest.util.HeaderUtil;
import com.payments.order.rest.util.ResponseUtil;
import com.payments.order.service.CustomerService;
import com.payments.order.service.OrderService;
import com.payments.order.service.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class OrderResource {
	
	private static final String ENTITY_NAME = "order";
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustomerService customerService;
	
    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @param orderorderDTO the orderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderDTO, or with status {@code 400 (Bad Request)} if the order has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
	@PostMapping("/orders")
	public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO)throws URISyntaxException{
		
        log.debug("REST request to save Order : {}", orderDTO);
        if (orderDTO.getId() != null) {
            throw new BadRequestException("A new order cannot already have an ID");
        }
        
        if(ObjectUtils.isEmpty(customerService.findById(orderDTO.getCustomerId()))) {
        	throw new NoSuchElementException("Uable to find the customer for this order, customer id :"+orderDTO.getCustomerId());
        }
        OrderDTO result = orderService.save(orderDTO);
		
		return ResponseEntity.created(new URI("/api/orders/" + result.getId())).header(ENTITY_NAME, result.getId().toString()).body(result);
	}
	
	
    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        log.debug("REST request to get all orders");
        return orderService.findAll();
    }

    
    /**
     * {@code GET  /order/:id} : get the "id" order.
     *
     * @param id the id of the order to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the order, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        Optional<Order> order = orderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(order);
    }
    
    /**
     * {@code GET  /orders/customer/:customerId} : get the "id" customer.
     *
     * @param id the id of the customer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/orders/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        log.debug("REST request to get Order by customer id : {}", customerId);

         if(ObjectUtils.isEmpty(customerService.findById(customerId))) {
        	throw new NoSuchElementException("Uable to find the customer, customer id :"+customerId);
          }
         List<Order> orders = orderService.getOrderByCustomerId(customerId);
		 HttpHeaders headers=HeaderUtil.createAlert(ENTITY_NAME, customerId.toString());	
         return new ResponseEntity<>(orders, headers, HttpStatus.OK);

    }
}
