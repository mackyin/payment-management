package com.payments.order.service;


import java.util.List;
import java.util.Optional;
import com.payments.order.domain.Payment;
import com.payments.order.service.dto.PaymentDTO;


/**
 * Service Interface for managing {@link com.payments.order.domain.Payment}.
 */
public interface PaymentService {
    /**
     * Save a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    PaymentDTO save(PaymentDTO paymentDTO);
    
    /**
     * Get all the payments.
     *
     * @return the list of entities.
     */
    List<Payment> findAll();

    /**
     * Get the "id" payment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Payment> findOne(Long id);
    
    /**
     * Get the "orderId" payment.
     *
     * @param orderId the orderId of the Payment entity.
     * @return the Payments matching to orderid.
     */ 
    public List<Payment> getAllPaymentsByOrderId(Long orderId);
    
}
