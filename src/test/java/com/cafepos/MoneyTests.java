package com.cafepos;

import com.cafepos.common.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTests {

    @Test
    void canAddTwoMoneyObjects() {
        Money m1 = Money.of(1.50);
        Money m2 = Money.of(2.75);
        assertEquals(Money.of(4.25), m1.add(m2));
    }

    @Test
    void canMultiplyMoneyByAnInteger() {
        Money price = Money.of(2.50);
        assertEquals(Money.of(7.50), price.multiply(3));
    }

    @Test
    void handlesZeroAndNegativeValues() {
        assertEquals(Money.of(0), Money.zero());
        assertThrows(IllegalArgumentException.class, () -> Money.of(-10.00));
    }

    @Test
    void roundsToTwoDecimalPlaces() {
        Money m = Money.of(1.2345);
        assertEquals(Money.of(1.23), m);
    }
}