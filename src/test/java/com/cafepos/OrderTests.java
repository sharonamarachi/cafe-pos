package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.OrderObserver;
import com.cafepos.order.LineItem;
import com.cafepos.order.Order;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTests {

    static class FakeObserver implements OrderObserver {
        final List<String> events = new ArrayList<>();

        @Override
        public void updated(Order order, String eventType) {
            events.add(eventType);
        }
    }

    @Test
    void observers_notified_on_item_add() {
        var p = new SimpleProduct("A", "A", Money.of(2.00));
        var o = new Order(1);

        o.addItem(new LineItem(p, 1));

        var obs = new FakeObserver();
        o.register(obs);

        o.addItem(new LineItem(p, 1));

        assertTrue(obs.events.contains("itemAdded"),
                "Should notify 'itemAdded' after adding an item");
    }

    @Test
    void multiple_observers_receive_ready() {
        var p = new SimpleProduct("B", "B", Money.of(3.00));
        var o = new Order(2);
        o.addItem(new LineItem(p, 1));

        var o1 = new FakeObserver();
        var o2 = new FakeObserver();
        o.register(o1);
        o.register(o2);

        o.markReady();

        assertTrue(o1.events.contains("ready"), "Observer 1 should receive 'ready'");
        assertTrue(o2.events.contains("ready"), "Observer 2 should receive 'ready'");
    }
}
