package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.order.LineItem;
import com.cafepos.order.Order;
import com.cafepos.payment.PaymentStrategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentDelegationTests {
    @Test
    void orderPayDelegatesToStrategy() {
        var p1 = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        var p2 = new SimpleProduct("P-CK",  "Cookie",   Money.of(3.50));
        var p3 = new SimpleProduct("P-SW",  "Sandwich", Money.of(6.00));
        var o = new Order(1001);
        o.addItem(new LineItem(p1, 2));
        o.addItem(new LineItem(p2, 1)); 
        o.addItem(new LineItem(p3, 3));


        final boolean[] called = { false };
        PaymentStrategy fake = order -> called[0] = true;

        o.pay(fake);

        assertTrue(called[0], "Order.pay() must delegate to the supplied PaymentStrategy");
    }
}

