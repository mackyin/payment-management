package com.payments.order.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.payments.order.domain.Order;
import com.payments.order.service.CustomerService;
import com.payments.order.service.dto.OrderDTO;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OrderMapper {
	
	@Autowired
	protected CustomerService customertomer;
	
	@Mappings({
	@Mapping(target = "customer", expression = "java(customertomer.findById(orderDTO.getCustomerId()))"),
	@Mapping(target = "netBalance", expression = "java(orderDTO.getBillAmount().negate())")//default net balance will be negative value when creating order
	})
	public abstract Order convertDtoToEntity(OrderDTO orderDTO);
	
	@Mapping(target="customerId", source="order.customer.id")
	public abstract OrderDTO entityToDto(Order order);

}
