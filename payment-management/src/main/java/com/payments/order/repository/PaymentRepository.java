package com.payments.order.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payments.order.domain.Order;
import com.payments.order.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>{
	
	   @Query("Select p From Payment p where p.order.id =:orderId")
	   public List<Payment> getAllPaymentsByOrderId(@Param("orderId") Long orderId);
	   
	   public List<Payment> findByOrder(Order order);

}
