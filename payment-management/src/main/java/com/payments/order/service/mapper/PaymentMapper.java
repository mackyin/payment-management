package com.payments.order.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import com.payments.order.domain.Payment;
import com.payments.order.service.OrderService;
import com.payments.order.service.dto.PaymentDTO;


/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PaymentMapper {
	
	@Autowired
	protected OrderService orderService;
	
	
	@Mappings({
	  @Mapping(target = "order", expression = "java(orderService.findById(paymentDTO.getOrderId()))"),
	})
	public abstract Payment convertDtoToEntity(PaymentDTO paymentDTO);
	
	@Mapping(target="orderId", source="payment.order.id")
	public abstract PaymentDTO convertEntityToDto(Payment payment);

}
