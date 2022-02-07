package com.payments.order.service.dto;

import java.math.BigDecimal;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


/**
 * A DTO for the {@link com.payments.order.domain.Customer} entity.
 */

@Getter
@Setter
public class CustomerDTO {
	
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @JsonIgnore
    private BigDecimal walletBalance=BigDecimal.ZERO;
  
}
