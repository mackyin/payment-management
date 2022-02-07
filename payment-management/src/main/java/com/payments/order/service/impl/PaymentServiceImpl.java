package com.payments.order.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.payments.order.domain.Customer;
import com.payments.order.domain.Order;
import com.payments.order.domain.Payment;
import com.payments.order.domain.enumeration.OrderStatus;
import com.payments.order.domain.enumeration.PaymentMethod;
import com.payments.order.domain.enumeration.PaymentStatus;
import com.payments.order.repository.PaymentRepository;
import com.payments.order.service.CustomerService;
import com.payments.order.service.OrderService;
import com.payments.order.service.PaymentService;
import com.payments.order.service.dto.PaymentDTO;
import com.payments.order.service.mapper.PaymentMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation for managing {@link Payment}.
 */
@Service
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService{

	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PaymentMapper paymentMapper;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustomerService customerService;
	
	
	@Override
	public PaymentDTO save(PaymentDTO paymentDTO) {
        log.debug("Request to save Payment : {}", paymentDTO);
        
        Payment payment = paymentMapper.convertDtoToEntity(paymentDTO);
        Payment payByWallet=null;
        Customer customer=customerService.findById(payment.getOrder().getCustomer().getId());
        
        if(paymentDTO.isUseWalletBalance() & customer.getWalletBalance().compareTo(BigDecimal.ZERO)>0) { //Reduce payment from customer wallet balance,using this flag so user can decide the payment
        	payByWallet=handlePayFromCustomerWallet(payment.getOrder(),customer); //creating payment entry under ZOOPLUS_WALLET payment method for tracking purpose
        }
        
        payment=handlePayment(payment,payByWallet); //Creating actual payment entry (CREDIT_CARD /BANK_TRANSFER) 
        
        return paymentMapper.convertEntityToDto(payment);
	}

	
    public Payment handlePayment(Payment payment,Payment payByWallet) {
    	Order order=orderService.findById(payment.getOrder().getId());
    	
    	BigDecimal cumalativePayment=payByWallet!=null? (payment.getPaidAmount().add(payByWallet.getPaidAmount())):payment.getPaidAmount();	//pay from wallet + Card / Bank transfer
    	BigDecimal netBalance=calculateNetBalance(cumalativePayment, order.getNetBalance());
    	
    	order.setCustomer(updateCustomer(order.getCustomer().getId(),netBalance,cumalativePayment));
    	order.setNetBalance(netBalance);
        order.setPaymentStatus(netBalance.compareTo(BigDecimal.ZERO)== 0 ? PaymentStatus.FULLY_PAID : (netBalance.compareTo(BigDecimal.ZERO)> 0? PaymentStatus.OVER_PAID:PaymentStatus.UNDER_PAID));
        order.setOrderStatus(!order.getPaymentStatus().equals(PaymentStatus.UNDER_PAID)?OrderStatus.COMPLETED:OrderStatus.PENDING);
       
        payment.setPaidAmount(payByWallet!=null?cumalativePayment.subtract(payByWallet.getPaidAmount()):payment.getPaidAmount());
        payment=paymentRepository.save(payment);
        
        orderService.save(order); 
        
        return payment;
    }
	
   /* 
    Payment reduction from wallet will be persist in DB for payment tracking purpose 
    */
	public Payment handlePayFromCustomerWallet(Order order,Customer customer) {
				
		Payment payment=new Payment();
		payment.setPaidAmount(customer.getWalletBalance());
		payment.setPaymentMethod(PaymentMethod.ZOOPLUS_WALLET);
		payment.setOrder(order);
		payment=paymentRepository.save(payment);
		return payment;
	}
   
    public Customer updateCustomer(Long customerId,BigDecimal netBalance,BigDecimal paidAmount) {
		Customer customer=customerService.findById(customerId);		
		customer.setWalletBalance(netBalance.compareTo(BigDecimal.ZERO)>0?netBalance:BigDecimal.ZERO);
		customer.setCustomerBalance(orderService.calculateNetBalanceByCustomerId(customer.getId(), OrderStatus.PENDING, paidAmount));
		return customer;	
    }
    
    public BigDecimal calculateNetBalance(BigDecimal paidAmount,BigDecimal netBalance) {  	
    	return netBalance=paidAmount.subtract(netBalance.abs());

    }


	@Override
	public List<Payment> findAll() {
		return paymentRepository.findAll();
	}


	@Override
	public Optional<Payment> findOne(Long id) {
		return paymentRepository.findById(id);
	}


	@Override
	public List<Payment> getAllPaymentsByOrderId(Long orderId) {
		Order order=orderService.findById(orderId);
		List<Payment> test=paymentRepository.findByOrder(order);
		return test;
	}

}
