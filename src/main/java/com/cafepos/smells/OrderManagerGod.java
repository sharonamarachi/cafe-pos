package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.domain.CardPayment;
import com.cafepos.domain.CashPayment;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.domain.PaymentStrategy;
import com.cafepos.domain.WalletPayment;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.DiscountPolicy;
import com.cafepos.pricing.FixedCouponDiscount;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.pricing.LoyaltyPercentDiscount;
import com.cafepos.pricing.NoDiscount;
import com.cafepos.pricing.ReceiptPrinter;
import com.cafepos.pricing.TaxPolicy;
import com.cafepos.catalog.Product;
import com.cafepos.pricing.PricingService;

public class OrderManagerGod {

    public final class OrderManager {
        private final ProductFactory factory;
        private final DiscountPolicy discountPolicy;
        private final TaxPolicy taxPolicy;
        private final ReceiptPrinter printer;
        private final PaymentStrategy paymentStrategy;

        public OrderManager(ProductFactory factory, DiscountPolicy discountPolicy,
                TaxPolicy taxPolicy, ReceiptPrinter printer, PaymentStrategy paymentStrategy) {
            this.factory = factory;
            this.discountPolicy = discountPolicy;
            this.taxPolicy = taxPolicy;
            this.printer = printer;
            this.paymentStrategy = paymentStrategy;
        }
    }

    public static String process(String recipe, int qty, String paymentType, String discountCode,
            boolean printReceipt) {
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

        PaymentStrategy payment = switch (paymentType == null ? "" : paymentType.toUpperCase()) {
            case "CASH" -> new CashPayment();
            case "CARD" -> new CardPayment("1234");
            case "WALLET" -> new WalletPayment("user-wallet-789");
            default -> order -> System.out.println("[UnknownPayment] " + order.totalWithTax(10));
        };

        Order dummyOrder = new Order(OrderIds.next());
        dummyOrder.addItem(new LineItem(product, qty));

        payment.pay(dummyOrder);

        ReceiptPrinter printer = new ReceiptPrinter();
        String receiptText = printer.format(recipe, qty,
                new PricingService.PricingResult(subtotal, discount, tax, total), 10);

        if (printReceipt)
            System.out.println(receiptText);
        return receiptText;

    }
}
