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
import com.payments.order.domain.Payment;
import com.payments.order.domain.enumeration.OrderStatus;
import com.payments.order.rest.errors.BadRequestException;
import com.payments.order.rest.util.HeaderUtil;
import com.payments.order.rest.util.ResponseUtil;
import com.payments.order.service.OrderService;
import com.payments.order.service.PaymentService;
import com.payments.order.service.dto.PaymentDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class PaymentResource {
	
	private static final String ENTITY_NAME = "Payment";
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private OrderService orderService;
	
    /**
     * {@code POST  /payments} : Create a new payment.
     *
     * @param paymentDTO the paymentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentDTO, or with status {@code 400 (Bad Request)} if the payment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
	@PostMapping("/payments")
	public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO)throws URISyntaxException{

        log.debug("REST request to save Payment : {}", paymentDTO);
        if (paymentDTO.getId() != null) {
            throw new BadRequestException("A new payment cannot already have an ID");
        }
        Order order=orderService.findById(paymentDTO.getOrderId());
        if(ObjectUtils.isEmpty(order) || (!ObjectUtils.isEmpty(order) && order.getOrderStatus().equals(OrderStatus.COMPLETED))) {
        	throw new BadRequestException("Cannot made payment to invalid / completed order");
        }
        
        PaymentDTO result = paymentService.save(paymentDTO);
        return ResponseEntity
            .created(new URI("/api/payments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);

	}
	
	
    /**
     * {@code GET  /payments} : get all the payments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of payments in body.
     */
    @GetMapping("/payments")
    public List<Payment> getAllPayments() {
        log.debug("REST request to get all Payments");
        return paymentService.findAll();
    }

    /**
     * {@code GET  /payment/:id} : get the "id" payment.
     *
     * @param id the id of the payment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the payment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/payments/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        log.debug("REST request to get Payment : {}", id);
        Optional<Payment> payment = paymentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(payment);
    }

    /**
     * {@code GET  /payments/:paymentId} : get the "id" order.
     *
     * @param orderId the orderId of the order to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Payment in body.
     */
    @GetMapping("/payments/order/{orderId}")
    public ResponseEntity<List<Payment>> getOrdersByCustomer(@PathVariable Long orderId) {
        log.debug("REST request to get Payments by order id : {}", orderId);
         List<Payment> payments = paymentService.getAllPaymentsByOrderId(orderId);
		 HttpHeaders headers=HeaderUtil.createAlert(ENTITY_NAME, payments.toString());	
         return new ResponseEntity<>(payments, headers, HttpStatus.OK);

    }

}
