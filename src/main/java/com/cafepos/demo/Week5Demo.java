package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.factory.ProductFactory;
import com.cafepos.order.LineItem;
import com.cafepos.order.Order;
import com.cafepos.order.OrderIds;

import java.util.Scanner;

public final class Week5Demo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProductFactory factory = new ProductFactory();
        Order order = new Order(OrderIds.next());

        System.out.println("Enter product codes (e.g., ESP+SHOT+OAT, LAT+L).");
        System.out.println("Press Enter on an empty line to finish.");

        while (true) {
            System.out.print("Product code: ");
            String code = scanner.nextLine().trim();

            if (code.isEmpty()) {
                break;
            }

            Product product = factory.create(code);
            if (product == null) {
                System.out.println("Product not found. Try again.");
                continue;
            }

            System.out.print("Quantity: ");
            String qtyInput = scanner.nextLine().trim();
            int quantity;
            try {
                quantity = Integer.parseInt(qtyInput);
                if (quantity <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
                continue;
            }

            order.addItem(new LineItem(product, quantity));
            System.out.println("Added " + product.name() + " x" + quantity);
        }

        System.out.println();
        System.out.println("Order Summary");
        System.out.println("Order #" + order.id());

        for (LineItem li : order.items()) {
            System.out.println(li.product().name() + " x" + li.quantity() + " = " + li.lineTotal());
        }

        System.out.println("Subtotal: " + order.subtotal());
        System.out.println("Tax (10%): " + order.taxAtPercent(10));
        System.out.println("Total: " + order.totalWithTax(10));

        scanner.close();
    }
}