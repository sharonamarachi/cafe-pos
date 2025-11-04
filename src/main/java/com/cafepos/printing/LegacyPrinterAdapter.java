package com.cafepos.printing;

import vendor.legacy.LegacyThermalPrinter;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class LegacyPrinterAdapter implements Printer {
    private final Consumer<byte[]> byteSink;

    public LegacyPrinterAdapter(LegacyThermalPrinter legacy) {
        this.byteSink = legacy::legacyPrint;
    }

    public LegacyPrinterAdapter(Consumer<byte[]> byteSink) {
        if (byteSink == null) throw new IllegalArgumentException("byteSink required");
        this.byteSink = byteSink;
    }

    @Override
    public void print(String text) {
        byte[] payload = text.getBytes(StandardCharsets.UTF_8);
        byteSink.accept(payload);
    }
}
