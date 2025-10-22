n this lab, I refactored OrderManagerGod to use smaller, focused classes like CheckoutService and PricingService.
This removed several code smells:

God Class: logic for pricing, discounts, tax, and receipts was all in one place.

Long Method: the process method did too many things.

Primitive Obsession: used raw strings and numbers for payment and discount logic.

Global State: used a shared variable (LAST_DISCOUNT_CODE).

Shotgun Surgery: small changes could break many parts of the code.

Refactorings Used

Extract Class: split the large God class into smaller ones (PricingService, CheckoutService, and discount classes).

Strategy Pattern: used for handling different discount and payment types.

Dependency Injection: passed objects through constructors instead of creating them inside the class, which makes testing easier and reduces coupling.

How the New Design Works

CheckoutService coordinates the process.

ProductFactory builds the coffee product (and decorators).

PricingService calculates subtotal, discount, tax, and total using:

DiscountPolicy (e.g. LoyaltyPercentDiscount, FixedCouponDiscount)

TaxPolicy (e.g. FixedRateTaxPolicy)

ReceiptPrinter formats the output text.

PaymentStrategy handles payment messages (like card or wallet).

SOLID Principles

Single Responsibility: each class has one clear purpose.

Open/Closed: new discounts or payments can be added without changing existing code—just make a new class.

Dependency Inversion: high-level code depends on interfaces (DiscountPolicy, PaymentStrategy) instead of concrete classes.

Example Extension

To add a new discount, just create a new class (e.g. SeasonalDiscount) that implements DiscountPolicy.
No need to change CheckoutService or PricingService — just plug it in.
