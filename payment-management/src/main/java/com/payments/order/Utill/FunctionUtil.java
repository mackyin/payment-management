package com.payments.order.Utill;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class FunctionUtil {	
	
	public static BiFunction<BigDecimal, BigDecimal, BigDecimal> calculateDifference = (amount1,amount2) -> amount1.subtract(amount2);
	

}
