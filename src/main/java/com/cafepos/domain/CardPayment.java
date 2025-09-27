package com.cafepos.domain;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;

    public CardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(Order order) {
        String maskedCard = maskCardNumber(cardNumber);
        System.out.println("[Card] Customer paid "
                + order.totalWithTax(10) 
                + " EUR with card " + maskedCard);
    }

    private String maskCardNumber(String number) {
        if (number == null || number.length() <= 4) {
            return number;
        }
        return "****" + number.substring(number.length() - 4);
    }

}
