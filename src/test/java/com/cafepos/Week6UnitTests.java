package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.smells.OrderManagerGod;
import com.cafepos.pricing.LoyaltyPercentDiscount;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.pricing.PricingService;
import com.cafepos.smells.CheckoutService;
import com.cafepos.pricing.ReceiptPrinter;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.NoDiscount;
import com.cafepos.pricing.FixedCouponDiscount;
import com.cafepos.pricing.DiscountPolicy;
import com.cafepos.pricing.TaxPolicy;




import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for refactored pricing components.
 * These tests verify isolated behavior without I/O or complex dependencies.
 */
public class Week6UnitTests {

    // ============================================================
    // DiscountPolicy Tests
    // ============================================================

    @Test
    void noDiscount_returns_zero() {
        DiscountPolicy d = new NoDiscount();
        assertEquals(Money.zero(), d.discountOf(Money.of(100.00)));
    }

    @Test
    void loyalty_discount_5_percent() {
        DiscountPolicy d = new LoyaltyPercentDiscount(5);
        assertEquals(Money.of(0.39), d.discountOf(Money.of(7.80)));
    }

    @Test
    void loyalty_discount_5_percent_on_100() {
        DiscountPolicy d = new LoyaltyPercentDiscount(5);
        assertEquals(Money.of(5.00), d.discountOf(Money.of(100.00)));
    }

    @Test
    void loyalty_discount_zero_percent() {
        DiscountPolicy d = new LoyaltyPercentDiscount(0);
        assertEquals(Money.zero(), d.discountOf(Money.of(100.00)));
    }

    @Test
    void loyalty_discount_rejects_negative_percent() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoyaltyPercentDiscount(-5));
    }

    @Test
    void fixed_coupon_discount_1_dollar() {
        DiscountPolicy d = new FixedCouponDiscount(Money.of(1.00));
        assertEquals(Money.of(1.00), d.discountOf(Money.of(3.30)));
    }

    @Test
    void fixed_coupon_caps_at_subtotal() {
        DiscountPolicy d = new FixedCouponDiscount(Money.of(10.00));
        // Coupon worth $10 on $3.30 subtotal should cap at $3.30
        assertEquals(Money.of(3.30), d.discountOf(Money.of(3.30)));
    }

    @Test
    void fixed_coupon_rejects_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new FixedCouponDiscount(null));
    }

    // ============================================================
    // TaxPolicy Tests
    // ============================================================

    @Test
    void fixed_rate_tax_10_percent() {
        TaxPolicy t = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(0.74), t.taxOn(Money.of(7.41)));
    }

    @Test
    void fixed_rate_tax_10_percent_on_100() {
        TaxPolicy t = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(10.00), t.taxOn(Money.of(100.00)));
    }

    @Test
    void fixed_rate_tax_zero_percent() {
        TaxPolicy t = new FixedRateTaxPolicy(0);
        assertEquals(Money.zero(), t.taxOn(Money.of(100.00)));
    }

    @Test
    void fixed_rate_tax_rejects_negative_percent() {
        assertThrows(IllegalArgumentException.class,
                () -> new FixedRateTaxPolicy(-10));
    }

    @Test
    void fixed_rate_tax_getPercent_returns_rate() {
        FixedRateTaxPolicy t = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(10.00), t.taxOn(Money.of(100.00)));
    }

    // ============================================================
    // PricingService Tests
    // ============================================================

    @Test
    void pricing_pipeline_with_loyalty_discount() {
        var pricing = new PricingService(
                new LoyaltyPercentDiscount(5),
                new FixedRateTaxPolicy(10));
        var pr = pricing.price(Money.of(7.80));

        // Verify discount: 7.80 * 5% = 0.39
        assertEquals(Money.of(0.39), pr.discount());

        // Verify discounted amount: 7.80 - 0.39 = 7.41
        assertEquals(Money.of(7.41),
                Money.of(pr.subtotal().asBigDecimal()
                        .subtract(pr.discount().asBigDecimal())));

        // Verify tax: 7.41 * 10% = 0.74
        assertEquals(Money.of(0.74), pr.tax());

        // Verify total: 7.41 + 0.74 = 8.15
        assertEquals(Money.of(8.15), pr.total());
    }

    @Test
    void pricing_pipeline_with_fixed_coupon() {
        var pricing = new PricingService(
                new FixedCouponDiscount(Money.of(1.00)),
                new FixedRateTaxPolicy(10));
        var pr = pricing.price(Money.of(3.30));

        // Verify discount: $1.00
        assertEquals(Money.of(1.00), pr.discount());

        // Verify discounted amount: 3.30 - 1.00 = 2.30
        Money discounted = Money.of(pr.subtotal().asBigDecimal()
                .subtract(pr.discount().asBigDecimal()));
        assertEquals(Money.of(2.30), discounted);

        // Verify tax: 2.30 * 10% = 0.23
        assertEquals(Money.of(0.23), pr.tax());

        // Verify total: 2.30 + 0.23 = 2.53
        assertEquals(Money.of(2.53), pr.total());
    }

    @Test
    void pricing_pipeline_no_discount() {
        var pricing = new PricingService(
                new NoDiscount(),
                new FixedRateTaxPolicy(10));
        var pr = pricing.price(Money.of(3.80));

        // Verify no discount
        assertEquals(Money.zero(), pr.discount());

        // Verify tax: 3.80 * 10% = 0.38
        assertEquals(Money.of(0.38), pr.tax());

        // Verify total: 3.80 + 0.38 = 4.18
        assertEquals(Money.of(4.18), pr.total());
    }

    @Test
    void pricing_service_rejects_null_discount_policy() {
        assertThrows(IllegalArgumentException.class,
                () -> new PricingService(null, new FixedRateTaxPolicy(10)));
    }

    @Test
    void pricing_service_rejects_null_tax_policy() {
        assertThrows(IllegalArgumentException.class,
                () -> new PricingService(new NoDiscount(), null));
    }

    @Test
    void pricing_handles_discount_exceeding_subtotal() {
        // Coupon of $10 on $5 subtotal should be capped
        var pricing = new PricingService(
                new FixedCouponDiscount(Money.of(10.00)),
                new FixedRateTaxPolicy(10));
        var pr = pricing.price(Money.of(5.00));

        // Discount capped at subtotal
        assertEquals(Money.of(5.00), pr.discount());

        // Discounted amount floored to zero
        assertEquals(Money.zero(),
                Money.of(pr.subtotal().asBigDecimal()
                        .subtract(pr.discount().asBigDecimal())));

        // Tax on zero is zero
        assertEquals(Money.zero(), pr.tax());

        // Total is zero
        assertEquals(Money.zero(), pr.total());
    }

    // ============================================================
    // ReceiptPrinter Tests
    // ============================================================

    @Test
    void receipt_printer_formats_with_discount() {
        ReceiptPrinter printer = new ReceiptPrinter();
        var result = new PricingService.PricingResult(
                Money.of(7.80),
                Money.of(0.39),
                Money.of(0.74),
                Money.of(8.15)
        );

        String receipt = printer.format("LAT+L", 2, result, 10);

        assertTrue(receipt.contains("Order (LAT+L) x2"));
        assertTrue(receipt.contains("Subtotal: 7.80"));
        assertTrue(receipt.contains("Discount: -0.39"));
        assertTrue(receipt.contains("Tax (10%): 0.74"));
        assertTrue(receipt.contains("Total: 8.15"));
    }

    @Test
    void receipt_printer_formats_without_discount() {
        ReceiptPrinter printer = new ReceiptPrinter();
        var result = new PricingService.PricingResult(
                Money.of(3.80),
                Money.zero(),
                Money.of(0.38),
                Money.of(4.18)
        );

        String receipt = printer.format("ESP+SHOT+OAT", 1, result, 10);

        assertTrue(receipt.contains("Order (ESP+SHOT+OAT) x1"));
        assertTrue(receipt.contains("Subtotal: 3.80"));
        assertFalse(receipt.contains("Discount:")); // No discount line
        assertTrue(receipt.contains("Tax (10%): 0.38"));
        assertTrue(receipt.contains("Total: 4.18"));
    }

    @Test
    void receipt_printer_preserves_exact_format() {
        ReceiptPrinter printer = new ReceiptPrinter();
        var result = new PricingService.PricingResult(
                Money.of(3.30),
                Money.of(1.00),
                Money.of(0.23),
                Money.of(2.53)
        );

        String receipt = printer.format("ESP+SHOT", 1, result, 10);

        // Verify exact line structure
        String[] lines = receipt.split("\n");
        assertEquals(5, lines.length); // 5 lines with discount
        assertEquals("Order (ESP+SHOT) x1", lines[0]);
        assertEquals("Subtotal: 3.30", lines[1]);
        assertEquals("Discount: -1.00", lines[2]);
        assertEquals("Tax (10%): 0.23", lines[3]);
        assertEquals("Total: 2.53", lines[4]);
    }
    }
