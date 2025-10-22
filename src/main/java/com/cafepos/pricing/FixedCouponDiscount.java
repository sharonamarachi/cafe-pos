package com.cafepos.pricing;

import com.cafepos.common.Money;

public final class FixedCouponDiscount implements DiscountPolicy {
    private final Money amount;

    public FixedCouponDiscount(Money amount) {

        if (amount == null) {
            throw new IllegalArgumentException("Coupon amount cannot be null");
        }
        // Optional but sensible: reject zero/negative coupons
        if (amount.asBigDecimal().signum() <= 0) {
            throw new IllegalArgumentException("Coupon amount must be > 0");
        }
        this.amount = amount;
    }

    @Override
    public Money discountOf(Money subtotal) {
        // cap at subtotal
        if (amount.asBigDecimal().compareTo(subtotal.asBigDecimal()) > 0)
            return subtotal;
        return amount;
    }
}