package com.cafepos.domain;

import com.cafepos.order.LineItem;
import com.cafepos.order.Order;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if (eventType.equals("itemAdded")) {
            var items = order.items();
            if (!items.isEmpty()) {
                LineItem lastItem = items.get(items.size() - 1);
                System.out.println("[Kitchen] Order #" + order.id() + ": " + 
                                   lastItem.quantity() + "x " + 
                                   lastItem.product().name() + " added");
            }
        } else if (eventType.equals("paid")) {
            System.out.println("[Kitchen] Order #" + order.id() + ": Payment received");   
        }
    }
}