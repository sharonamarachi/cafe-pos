package com.cafepos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.cafepos.smells.OrderManagerGod;

public class DiscountPolicyCharacterizationTests {

    @Test
    void unknownDiscountCode_appliesNoDiscount() {
        String receipt = OrderManagerGod.process("ESP+SHOT+OAT", 1, "CASH", "INVALID", false);

        assertFalse(receipt.contains("Discount:"), "Unexpected discount line for an invalid code");

        assertTrue(receipt.contains("Subtotal: 3.90"));
        assertTrue(receipt.contains("Tax (10%): 0.39"));
        assertTrue(receipt.contains("Total: 4.29"));
    }

    @Test
    void loyaltyDiscount_appliesFivePercent() {
        String receipt = OrderManagerGod.process("LAT+L", 2, "CARD", "LOYAL5", false);

        assertTrue(receipt.contains("Discount: -0.40"), "Expected loyalty discount line missing");

        assertTrue(receipt.contains("Subtotal: 7.90"));
        assertTrue(receipt.contains("Tax (10%): 0.75"));
        assertTrue(receipt.contains("Total: 8.25"));
    }

    @Test
    void fixedCoupon_discountReducesTotal() {
        String receipt = OrderManagerGod.process("ESP+SHOT", 1, "WALLET", "COUPON1", false);

        assertTrue(receipt.contains("Discount: -1.00"), "Fixed coupon discount not applied");

        assertTrue(receipt.contains("Subtotal: 3.40"));
        assertTrue(receipt.contains("Tax (10%): 0.24"));
        assertTrue(receipt.contains("Total: 2.64"));
    }
}
