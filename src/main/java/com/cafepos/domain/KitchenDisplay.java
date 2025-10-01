package com.cafepos.domain;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        // TODO: on "itemAdded" -> print "[Kitchen] Order
        /*
         * #<id>: item added"
         * // on "paid" -> print "[Kitchen] Order
         * #<id>: payment received"
         */
        if (eventType.equals("itemAdded")) {
            System.out.println("[Kitchen] Order #" + order.id() + ": item added");
        } else if (eventType.equals("paid")) {
            System.out.println("[Kitchen] Order #" + order.id() + ": Payment received");   
        }
    }
}
