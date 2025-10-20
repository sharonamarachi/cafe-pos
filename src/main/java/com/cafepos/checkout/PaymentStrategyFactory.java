package com.cafepos.checkout;

import com.cafepos.domain.CardPayment;
import com.cafepos.domain.CashPayment;
import com.cafepos.domain.PaymentStrategy;
import com.cafepos.domain.WalletPayment;

/**
 * Factory to resolve PaymentStrategy from payment type string.
 * Replaces the string-based conditional in OrderManagerGod.
 */
public final class PaymentStrategyFactory {


    public static PaymentStrategy fromType(String paymentType) {
        if (paymentType == null) {
            return null;
        }

        return switch (paymentType.toUpperCase()) {
            case "CASH" -> new CashPayment();
            case "CARD" -> new CardPayment("1234"); 
            case "WALLET" -> new WalletPayment("user-wallet-789");
            default -> null; // Unknown payment types
        };
    }
}