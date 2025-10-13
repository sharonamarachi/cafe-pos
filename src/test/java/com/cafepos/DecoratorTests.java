package com.cafepos;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;

import com.cafepos.decorator.ExtraShot;
import com.cafepos.decorator.OatMilk;
import com.cafepos.decorator.SizeLarge;
import com.cafepos.factory.ProductFactory;

import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.catalog.Priced;

import com.cafepos.catalog.Product;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DecoratorTests {

    @Test
    void decorator_single_addon() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product withShot = new ExtraShot(espresso);

        assertEquals("Espresso + Extra Shot", withShot.name());
        assertEquals(Money.of(3.30), ((Priced) withShot).price());
    }

    @Test
    void decorator_stacks() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product decorated = new SizeLarge(new OatMilk(new ExtraShot(espresso)));

        assertEquals("Espresso + Extra Shot + Oat Milk (Large)", decorated.name());
        assertEquals(Money.of(4.50), ((Priced) decorated).price());
    }

    @Test
    void factory_parses_recipe() {
        ProductFactory f = new ProductFactory();
        Product p = f.create("ESP+SHOT+OAT");

        assertTrue(p.name().contains("Espresso"));
        assertTrue(p.name().contains("Oat Milk"));
    }

    @Test
    void order_uses_decorated_price() {
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        Product withShot = new ExtraShot(espresso); // 2.50 + 0.80 = 3.30

        Order o = new Order(1);
        o.addItem(new LineItem(withShot, 2));

        assertEquals(Money.of(6.60), o.subtotal());

    }
}
