package com.cafepos.demo;

import java.util.Scanner;

import com.cafepos.smells.OrderManagerGod;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.PricingService;
import com.cafepos.pricing.LoyaltyPercentDiscount;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.pricing.ReceiptPrinter;
import com.cafepos.smells.CheckoutService;

public final class Week6Demo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- User inputs ---
        System.out.print("Enter product code (e.g., LAT+L): ");
        String productCode = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter payment type (CASH, CARD, WALLET): ");
        String paymentType = scanner.nextLine();

        System.out.print("Enter discount code (LOYAL5, COUPON1, or NONE): ");
        String discountCode = scanner.nextLine();

        System.out.println("\n--- Generating receipts ---\n");

        // --- Old behavior ---
        String oldReceipt = OrderManagerGod.process(
                productCode,
                quantity,
                paymentType,
                discountCode,
                false
        );

        // --- New behavior with CheckoutService ---
        var pricing = new PricingService(
                new LoyaltyPercentDiscount(5),
                new FixedRateTaxPolicy(10)
        );
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(
                new ProductFactory(),
                pricing,
                printer,
                10
        );

        String newReceipt = checkout.checkout(productCode, quantity);

        // --- Output ---
        System.out.println("Old Receipt:\n" + oldReceipt);
        System.out.println("\nNew Receipt:\n" + newReceipt);
        System.out.println("\nReceipts match exactly? " + oldReceipt.equals(newReceipt));

        scanner.close();
    }
}
