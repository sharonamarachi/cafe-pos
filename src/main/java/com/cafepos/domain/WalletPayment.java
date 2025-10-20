package com.cafepos.domain;

public final class WalletPayment implements PaymentStrategy {
    private final String walletId;

    public WalletPayment(String walletId) {
        this.walletId = walletId;
    }

    @Override
    public void pay(Order order) {
        System.out.println("[Wallet] Customer paid " +
                order.totalWithTax(10) +
                " EUR via wallet " + walletId);
    }
}