package com.cafepos.domain;

import com.cafepos.common.Money;
import java.util.ArrayList;
import java.util.List;

public final class Order {
    private final long id;

    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public void addItem(LineItem li) {
        // Original logic:
        if (li == null)
            throw new IllegalArgumentException("line item required");
        if (li.quantity() <= 0)
            throw new IllegalArgumentException("quantity must be > 0");
        items.add(li);

        // New Observer logic:
        notifyObservers("itemAdded");
    }

    public List<LineItem> items() {
        return List.copyOf(items);
    }

    public Money subtotal() {
        return items.stream()
                .map(LineItem::lineTotal)
                .reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0)
            throw new IllegalArgumentException("percent must be >= 0");
        return subtotal().percentage(percent);
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public long id() {
        return id;
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null)
            throw new IllegalArgumentException("strategy required");
        strategy.pay(this);

        // Notify observers of payment
        notifyObservers("paid");
    }

    public void register(OrderObserver o) {
        // TODO: add null check and add the observer, avoiding duplicates
        if (o == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public void unregister(OrderObserver o) {
        // TODO: remove the observer if present
        if (o != null) {
            observers.remove(o);
        }
    }

    // 2) Publish events
    private void notifyObservers(String eventType) {
        for (OrderObserver observer : observers) {
            observer.updated(this, eventType);
        }
    }

    public void markReady() {
        // TODO: just publish notifyObservers("ready")
        // 1. Change State (If Order had a 'status' field, it would be set here, but
        // since it doesn't,
        // we just publish the event as instructed)

        // 2. Announce Change
        notifyObservers("ready");
    }

}
