package com.cafepos.pricing;

import com.cafepos.common.Money;

public final class FixedCouponDiscount implements DiscountPolicy {
    private final Money amount;

    public FixedCouponDiscount(Money amount) {
        if (amount == null)
            throw new IllegalArgumentException("amount required");  // 👈 this line fixes the test
        if (amount.asBigDecimal().signum() <= 0)
            throw new IllegalArgumentException("amount must be positive");
        this.amount = amount;
    }

    @Override
    public Money discountOf(Money subtotal) {
        if (subtotal == null)
            throw new IllegalArgumentException("subtotal required");
        // Cap discount at subtotal
        return amount.asBigDecimal().compareTo(subtotal.asBigDecimal()) > 0 ? subtotal : amount;
    }
}