package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.menu.Menu;
import com.cafepos.menu.MenuComponent;
import com.cafepos.menu.MenuItem;
import com.cafepos.state.OrderFSM;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Week9Tests {

    @Test
    void depth_first_traversal_collects_nodes_in_expected_order() {
        Menu root = new Menu("CAFÉ MENU");
        Menu drinks = new Menu(" Drinks ");
        Menu coffee = new Menu("  Coffee ");
        Menu desserts = new Menu(" Desserts ");

        coffee.add(new MenuItem("Espresso", Money.of(2.50), true));
        coffee.add(new MenuItem("Latte (Large)", Money.of(3.90), true));
        drinks.add(coffee);

        desserts.add(new MenuItem("Cheesecake", Money.of(3.50), false));
        desserts.add(new MenuItem("Oat Cookie", Money.of(1.20), true));

        root.add(drinks);
        root.add(desserts);

      
        List<String> seen = new ArrayList<>();
        seen.add(root.name());
        Iterator<MenuComponent> it = root.iterator();
        while (it.hasNext()) seen.add(it.next().name());

        assertEquals(List.of(
                "CAFÉ MENU",
                " Drinks ",
                "  Coffee ",
                "Espresso",
                "Latte (Large)",
                " Desserts ",
                "Cheesecake",
                "Oat Cookie"
        ), seen, "Depth-first order should follow nested menus then items");

    }

    @Test
    void vegetarian_filter_returns_only_veg_items() {
        Menu root = new Menu("CAFÉ MENU");
        Menu coffee = new Menu("  Coffee ");
        Menu desserts = new Menu(" Desserts ");

        coffee.add(new MenuItem("Espresso", Money.of(2.50), true));
        coffee.add(new MenuItem("Latte (Large)", Money.of(3.90), true));
        desserts.add(new MenuItem("Cheesecake", Money.of(3.50), false));
        desserts.add(new MenuItem("Oat Cookie", Money.of(1.20), true));
        root.add(coffee);
        root.add(desserts);

        var veg = root.vegetarianItems();
        var names = veg.stream().map(MenuItem::name).toList();

        assertEquals(List.of("Espresso", "Latte (Large)", "Oat Cookie"), names);
    }



    @Test
    void order_fsm_happy_path() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());

        fsm.pay();         // NEW -> PREPARING
        assertEquals("PREPARING", fsm.status());

        fsm.markReady();   // PREPARING -> READY
        assertEquals("READY", fsm.status());

        fsm.deliver();     // READY -> DELIVERED
        assertEquals("DELIVERED", fsm.status());
    }

    @Test
    void illegal_transitions_leave_state_unchanged() {
        OrderFSM fsm = new OrderFSM();
        fsm.prepare();
        assertEquals("NEW", fsm.status());
        fsm.markReady();
        assertEquals("NEW", fsm.status());
        fsm.deliver();
        assertEquals("NEW", fsm.status());

        // Cancel from NEW is allowed → CANCELLED, then all actions keep CANCELLED
        fsm.cancel();
        assertEquals("CANCELLED", fsm.status());

        fsm.pay();
        fsm.prepare();
        fsm.markReady();
        fsm.deliver();
        fsm.cancel();
        assertEquals("CANCELLED", fsm.status());
    }
}
