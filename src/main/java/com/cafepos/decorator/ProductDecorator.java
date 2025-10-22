// src/main/java/com/cafepos/decorator/ProductDecorator.java
package com.cafepos.decorator;

import com.cafepos.catalog.Product;
import com.cafepos.catalog.Priced;   // <-- keep this the SAME everywhere

/**
 * Minimal base for product decorators.
 * Forwards Product fields; subclasses decide how price() changes.
 */
public abstract class ProductDecorator implements Product, Priced {
    protected final Product base;
    protected final Priced pricedBase;

    protected ProductDecorator(Product base) {
        if (base == null) throw new IllegalArgumentException("base required");
        if (!(base instanceof Priced))
            throw new IllegalArgumentException("base must implement Priced");
        this.base = base;
        this.pricedBase = (Priced) base;
    }

    // ---- Product ----
    @Override public String id()        { return base.id(); }
    @Override public String name()      { return base.name(); }           // subclasses append text
    @Override public com.cafepos.common.Money basePrice() { return base.basePrice(); }

    // ---- Priced ----
    @Override public com.cafepos.common.Money price()     { return pricedBase.price(); } // default
}
