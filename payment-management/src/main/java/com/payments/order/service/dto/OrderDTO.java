package com.payments.order.service.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.payments.order.domain.enumeration.OrderStatus;
import com.payments.order.domain.enumeration.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.payments.order.domain.Order} entity.
 */

@Data
@NoArgsConstructor
public class OrderDTO implements Serializable {
	
    private Long id;

    @NotNull
    private BigDecimal billAmount;
    
    @NotNull
    private Long customerId;
    
    @JsonIgnore
    private OrderStatus orderStatus=OrderStatus.PENDING;
    
    @JsonIgnore
    private PaymentStatus paymentStatus=PaymentStatus.UNDER_PAID;

}
