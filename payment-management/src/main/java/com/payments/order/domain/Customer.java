package com.payments.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
public class Customer implements Serializable{
	
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotNull
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    
    @Column(name = "customer_balance")
    private BigDecimal customerBalance=BigDecimal.ZERO;
    
    @Column(name = "wallet_balance")
    private BigDecimal walletBalance=BigDecimal.ZERO;
    
    @JsonIgnore
    @Version
    private int version;
  

}
