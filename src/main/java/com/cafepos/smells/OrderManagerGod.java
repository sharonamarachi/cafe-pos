package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.catalog.Product;

public class OrderManagerGod {
    public static int TAX_PERCENT = 10;
    public static String LAST_DISCOUNT_CODE = null;

    public static String process(String recipe, int qty, String paymentType,
            String discountCode, boolean printReceipt) {

        // SMELL: God Class & Long Method - This method handles creation, pricing,
        // discounting, tax calculation, payment processing, AND receipt formatting.
        // Multiple responsibilities should be separated into cohesive classes.

        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);

        Money unitPrice;
        try {
            var priced = product instanceof com.cafepos.decorator.Priced p
                    ? p.price()
                    : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }

        if (qty <= 0)
            qty = 1;

        Money subtotal = unitPrice.multiply(qty);

        // SMELL: Primitive Obsession - discount codes as raw strings; magic number 5 for LOYAL5 rate.
        // SMELL: Shotgun Surgery risk - adding a new discount type requires editing this method.
        // SMELL: Duplicated Logic - BigDecimal arithmetic repeated inline.

        Money discount = Money.zero();
        if (discountCode != null) {
            if (discountCode.equalsIgnoreCase("LOYAL5")) {
                // SMELL: percentage) duplicated BigDecimal manipulation.
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5))
                        .divide(java.math.BigDecimal.valueOf(100)));
            } else if (discountCode.equalsIgnoreCase("COUPON1")) {
                // SMELL: (fixed coupon amount) hardcoded discount logic.
                discount = Money.of(1.00);
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            // SMELL: Global/Static State - LAST_DISCOUNT_CODE mutated; hard to test.
            LAST_DISCOUNT_CODE = discountCode;
        }

        Money discounted = Money.of(subtotal.asBigDecimal()
                .subtract(discount.asBigDecimal()));
        if (discounted.asBigDecimal().signum() < 0)
            discounted = Money.zero();

        // SMELL: Primitive Obsession - TAX_PERCENT as static primitive; magic numbers 100 in division.
        // SMELL: Shotgun Surgery risk - changing tax rate or formula requires editing this method.
        // SMELL: Duplicated Logic - BigDecimal multiply or divide pattern repeated.
        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
                .divide(java.math.BigDecimal.valueOf(100)));
        var total = discounted.add(tax);

        // SMELL: Feature Envy / Shotgun Surgery - Payment type as raw string; I/O logic embedded.
        // SMELL: Hard to extend: adding a new payment method requires editing this method.
        if (paymentType != null) {
            if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("[Cash] Customer paid " + total + " EUR");
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }
        }

        // SMELL: God Class - Receipt formatting logic inline; should be delegated.
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(subtotal).append("\n");
        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        // SMELL: Primitive Obsession - TAX_PERCENT in receipt string format.
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total);

        String out = receipt.toString();
        if (printReceipt) {
            System.out.println(out);
        }
        return out;
    }
}