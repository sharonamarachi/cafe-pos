package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.order.LineItem;
import com.cafepos.order.Order;
import com.cafepos.catalog.Catalog;
import org.junit.jupiter.api.Test;
import com.cafepos.catalog.InMemoryCatalog;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class OrderTotalsTests {
    private Catalog catalog;

    @BeforeEach
    void setup() {
        catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
    }

    @Test
    void calculateOrderTotalsCorrectly() {
        Order o = new Order(1);
        o.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
        o.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));

        // Subtotal: (2 * $2.50) + (1 * $3.50) = $8.50
        assertEquals(Money.of(8.50), o.subtotal());

        // Tax: 10% of $8.50 = $0.85
        assertEquals(Money.of(0.85), o.taxAtPercent(10));

        // Total: $8.50 + $0.85 = $9.35
        assertEquals(Money.of(9.35), o.totalWithTax(10));
    }
}