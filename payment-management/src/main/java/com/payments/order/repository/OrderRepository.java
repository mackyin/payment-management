package com.payments.order.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payments.order.domain.Order;
import com.payments.order.domain.enumeration.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	
   @Modifying
   @Query("update Order o set o.netBalance = :balanceAmount where o.id = :orderId")
   void updateOrderBalanceAmount(BigDecimal balanceAmount,Long orderId);
   
   @Query("SELECT SUM(o.netBalance) FROM Order o where o.customer.id =:customerId and o.orderStatus=:orderStatus")
   public BigDecimal calculateNetBalanceByCustomerId(@Param("customerId") Long customerId,@Param("orderStatus") OrderStatus orderStatus);
	
   @Query("Select o From Order o where o.customer.id =:customerId")
   public List<Order> getOrderByCustomerId(@Param("customerId") Long customerId);

}
