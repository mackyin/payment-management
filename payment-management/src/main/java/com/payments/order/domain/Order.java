package com.payments.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.payments.order.domain.enumeration.OrderStatus;
import com.payments.order.domain.enumeration.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item") 
@Data
@NoArgsConstructor
public class Order implements Serializable{
	
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bill_amount", precision = 21, scale = 2)
    private BigDecimal billAmount;

    @Column(name = "net_balance", precision = 21, scale = 2)
    private BigDecimal netBalance;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Customer customer;
    
    @JsonIgnore
    @Version
    private int version;

}
