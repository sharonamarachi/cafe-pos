package com.cafepos.state;

final class States implements State {
    @Override
    public void pay(OrderFSM ctx) {
        System.out.println("[State] Paid → Preparing");
        ctx.set(new PreparingState());
    }

    @Override
    public void prepare(OrderFSM ctx) {
        System.out.println("[State] Cannot prepare before pay");
    }

    @Override
    public void markReady(OrderFSM ctx) {
        System.out.println("[State] Not ready yet");
    }

    @Override
    public void deliver(OrderFSM ctx) {
        System.out.println("[State] Cannot deliver yet");
    }

    @Override
    public void cancel(OrderFSM ctx) {
        System.out.println("[State] Cancelled");
        ctx.set(new CancelledState());
    }

    @Override
    public String name() {
        return "NEW";
    }
}

final class PreparingState implements State {
    @Override
    public void pay(OrderFSM ctx) {
        System.out.println("[State] Already paid");
    }

    @Override
    public void prepare(OrderFSM ctx) {
        System.out.println("[State] Still preparing...");
    }

    @Override
    public void markReady(OrderFSM ctx) {
        System.out.println("[State] Ready for pickup");
        ctx.set(new ReadyState());
    }

    @Override
    public void deliver(OrderFSM ctx) {
        System.out.println("[State] Deliver not allowed before ready");
    }

    @Override
    public void cancel(OrderFSM ctx) {
        System.out.println("[State] Cancelled during prep");
        ctx.set(new CancelledState());
    }

    @Override
    public String name() {
        return "PREPARING";
    }
}

final class ReadyState implements State {
    @Override
    public void pay(OrderFSM ctx) {
        System.out.println("[State] Already paid");
    }

    @Override
    public void prepare(OrderFSM ctx) {
        System.out.println("[State] Already prepared");
    }

    @Override
    public void markReady(OrderFSM ctx) {
        System.out.println("[State] Already ready");
    }

    @Override
    public void deliver(OrderFSM ctx) {
        System.out.println("[State] Delivered");
        ctx.set(new DeliveredState());
    }

    @Override
    public void cancel(OrderFSM ctx) {
        System.out.println("[State] Cannot cancel after ready");
    }

    @Override
    public String name() {
        return "READY";
    }
}

final class DeliveredState implements State {
    @Override
    public void pay(OrderFSM ctx) {
        System.out.println("[State] Completed");
    }

    @Override
    public void prepare(OrderFSM ctx) {
        System.out.println("[State] Completed");
    }

    @Override
    public void markReady(OrderFSM ctx) {
        System.out.println("[State] Completed");
    }

    @Override
    public void deliver(OrderFSM ctx) {
        System.out.println("[State] Already delivered");
    }

    @Override
    public void cancel(OrderFSM ctx) {
        System.out.println("[State] Completed");
    }

    @Override
    public String name() {
        return "DELIVERED";
    }
}

final class CancelledState implements State {
    @Override
    public void pay(OrderFSM ctx) {
        System.out.println("[State] Cancelled");
    }

    @Override
    public void prepare(OrderFSM ctx) {
        System.out.println("[State] Cancelled");
    }

    @Override
    public void markReady(OrderFSM ctx) {
        System.out.println("[State] Cancelled");
    }

    @Override
    public void deliver(OrderFSM ctx) {
        System.out.println("[State] Cancelled");
    }

    @Override
    public void cancel(OrderFSM ctx) {
        System.out.println("[State] Already cancelled");
    }

    @Override
    public String name() {
        return "CANCELLED";
    }
}