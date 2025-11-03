package com.cafepos.demo;

import java.util.Scanner;

import com.cafepos.smells.OrderManagerGod;
import com.cafepos.checkout.PricingService;
import com.cafepos.checkout.ReceiptPrinter;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.LoyaltyPercentDiscount;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.smells.CheckoutService;
public final class Week6Demo {
    public static void main(String[] args) {
        // Old behavior
        String oldReceipt = OrderManagerGod.process("LAT+L", 2, "CARD",
                "LOYAL5", false);
        // New behavior with equivalent result
        var pricing = new PricingService(new LoyaltyPercentDiscount(5), new FixedRateTaxPolicy(10));
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);
        String newReceipt = checkout.checkout("LAT+L", 2);
        System.out.println("Old Receipt:\n" + oldReceipt);
        System.out.println("\nNew Receipt:\n" + newReceipt);
        System.out.println("\nMatch: " + oldReceipt.equals(newReceipt));
    }
}