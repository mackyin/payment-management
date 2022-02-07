package com.payments.order.Util;

import java.math.BigDecimal;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class NumberMatcher extends TypeSafeMatcher<Number> {

    final BigDecimal value;

    public NumberMatcher(BigDecimal value) {
        this.value = value;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a numeric value is ").appendValue(value);
    }

    @Override
    protected boolean matchesSafely(Number item) {
        BigDecimal bigDecimal = asDecimal(item);
        return bigDecimal != null && value.compareTo(bigDecimal) == 0;
    }

    private static BigDecimal asDecimal(Number item) {
        if (item == null) {
            return null;
        }
        if (item instanceof BigDecimal) {
            return (BigDecimal) item;
        } else if (item instanceof Long) {
            return BigDecimal.valueOf((Long) item);
        } else if (item instanceof Integer) {
            return BigDecimal.valueOf((Integer) item);
        } else if (item instanceof Double) {
            return BigDecimal.valueOf((Double) item);
        } else if (item instanceof Float) {
            return BigDecimal.valueOf((Float) item);
        } else {
            return BigDecimal.valueOf(item.doubleValue());
        }
    }
}
