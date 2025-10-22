package com.cafepos.smells;

import com.cafepos.catalog.Product;
import com.cafepos.catalog.Priced;     // <-- critical: use the SAME Priced your products implement
import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;

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

        Money subtotal = unit.multiply(quantity);

        String code = (discountCode == null ? "NONE" : discountCode.trim().toUpperCase());
        Money discount = Money.zero();
        switch (code) {
            case "LOYAL5":
                discount = subtotal.multiplyPercent(5);
                break;
            case "COUPON1":
                Money one = Money.of(1.00);
                discount = one.asBigDecimal().compareTo(subtotal.asBigDecimal()) > 0 ? subtotal : one;
                break;
            case "NONE":
            default:
                code = "NONE";
                discount = Money.zero();
        }
        LAST_DISCOUNT_CODE = code;

        Money discounted = subtotal.minus(discount);
        Money tax = discounted.multiplyPercent(TAX_PERCENT);
        Money total = discounted.add(tax);

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
