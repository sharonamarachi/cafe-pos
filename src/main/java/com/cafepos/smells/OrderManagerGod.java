package com.cafepos.smells;

import com.cafepos.catalog.Product;
import com.cafepos.checkout.DiscountPolicy;
import com.cafepos.checkout.TaxPolicy;
import com.cafepos.catalog.Priced; 
import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.FixedCouponDiscount;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.pricing.LoyaltyPercentDiscount;
import com.cafepos.pricing.NoDiscount;

public class OrderManagerGod {

    public static int TAX_PERCENT = 10;
    public static String LAST_DISCOUNT_CODE = "NONE";

    public static String process(String recipe, int qty,
                                 String paymentType, String discountCode,
                                 boolean debug) {

        int quantity = Math.max(1, qty);

        Product p = new ProductFactory().create(recipe);
        // Use the correct Priced interface so decorators are included
        Money unit = (p instanceof Priced priced) ? priced.price() : p.basePrice();

        Money subtotal = unit.multiply(qty);

        DiscountPolicy discountPolicy = switch (discountCode == null ? "" : discountCode.toUpperCase()) {
            case "LOYAL5" -> new LoyaltyPercentDiscount(5);
            case "COUPON1" -> new FixedCouponDiscount(Money.of(1.00));
            default -> new NoDiscount();
        };

        Money discount = discountPolicy.discountOf(subtotal);

        Money discounted = Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
        if (discounted.asBigDecimal().signum() < 0)
            discounted = Money.zero();

        TaxPolicy taxPolicy = new FixedRateTaxPolicy(10);
        Money tax = taxPolicy.taxOn(discounted);
        var total = discounted.add(tax);


        StringBuilder sb = new StringBuilder();
        sb.append("Order (").append(recipe == null ? "" : recipe.trim()).append(") x").append(quantity).append('\n');
        sb.append("Subtotal: ").append(subtotal).append('\n');
        if (discount.asBigDecimal().signum() > 0) {
            sb.append("Discount: -").append(discount).append('\n');
        }
        sb.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append('\n');
        sb.append("Total: ").append(total);

        printPayment(paymentType, total);
        return sb.toString();
    }

    private static void printPayment(String paymentType, Money total) {
        String kind = paymentType == null ? "CASH" : paymentType.trim().toUpperCase();
        switch (kind) {
            case "CARD":
                System.out.println("[Card] Customer paid " + total + " EUR with card 1234");
                break;
            case "WALLET":
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
                break;
            default:
                System.out.println("[Cash] Customer paid " + total + " EUR");
        }
    }
}
