package com.cafepos;

import com.cafepos.command.*;
import com.cafepos.order.Order;
import com.cafepos.order.OrderIds;
import com.cafepos.payment.CardPayment;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class week8Test {

    @Test
    void invoker_executes_command_and_undo_reverts_last() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(2);

        remote.setSlot(0, new AddItemCommand(service, "ESP+SHOT", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));

        remote.press(0); // +1
        remote.press(1); // +2

        assertEquals(3, order.items().stream().mapToInt(li -> li.quantity()).sum());

        remote.undo(); // undo LAT+L x2

        assertEquals(1, order.items().stream().mapToInt(li -> li.quantity()).sum(),
                "Undo must reverse LAST command (LIFO)");
    }

    @Test
    void macro_executes_all_and_undo_reverts_entire_batch_in_reverse_order() {
        Order order = new Order(OrderIds.next());
        OrderService svc = new OrderService(order);

        // Use different items so we can also assert reverse-order via a simple log if needed
        Command addEsp = new AddItemCommand(svc, "ESP+SHOT", 1); // first
        Command addLat = new AddItemCommand(svc, "LAT+L", 1);    // last

        MacroCommand macro = new MacroCommand(addEsp, addLat);

        macro.execute();
        assertEquals(2, order.items().stream().mapToInt(li -> li.quantity()).sum(),
                "Macro execute should apply all inner commands");

        macro.undo(); // should undo BOTH inner commands, in reverse order
        assertEquals(0, order.items().stream().mapToInt(li -> li.quantity()).sum(),
                "Macro undo must revert the entire batch (atomic) in reverse order");
    }

    @Test
    void adapter_converts_text_to_bytes_and_calls_sink() {
        AtomicInteger lastLen = new AtomicInteger(-1);
        Printer printer = new LegacyPrinterAdapter(bytes -> lastLen.set(bytes.length));

        printer.print("ABC"); // UTF-8 length should be >= 3

        assertTrue(lastLen.get() >= 3,
                "Adapter must convert text to byte[] before calling legacy/sink");
    }

    @Test
    void end_to_end_button_flow_matches_week5_prices() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(3);

        remote.setSlot(0, new AddItemCommand(service, "ESP+SHOT+OAT", 1)); // 3.80
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));       // 7.80
        remote.setSlot(2, new PayOrderCommand(service,
                new CardPayment("1234567890123456"), 20));

        remote.press(0);
        remote.press(1);
        remote.undo();     // remove LAT+L x2
        remote.press(1);    // add again

        assertEquals("11.60",
                order.subtotal().asBigDecimal().toPlainString(),
                "Subtotal must equal Week-5 pricing math (3.80 + 7.80)");
    }
}
