package com.payments.order.service.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.payments.order.domain.enumeration.PaymentMethod;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;


/**
 * A DTO for the {@link com.payments.order.domain.Payment} entity.
 */
@Data
@NoArgsConstructor
public class PaymentDTO implements Serializable {

	
    private Long id;

    private BigDecimal paidAmount;

    @NotNull
    private PaymentMethod paymentMethod;
 
    @NotNull
    private Long orderId;
    
    @JsonIgnore
    private boolean useWalletBalance=true;


   
}
