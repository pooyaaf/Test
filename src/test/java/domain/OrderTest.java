package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;

    @BeforeEach
    void setup() {
        order = new Order();
        order.setId(1);
        order.setCustomer(1);
        order.setPrice(100);
        order.setQuantity(10);
    }

    @AfterEach
    void tearDown() {
        order = null;
    }

    @Test
    void testEquals_IsOrderSameID() {
        Order order2 = new Order();
        order2.setId(1);
        assertTrue(order.equals(order2));
    }

    @Test
    void testEquals_IsOrderDifferentID() {
        Order order2 = new Order();
        order2.setId(2);
        assertFalse(order.equals(order2));
    }

    @Test
    void testEquals_IsNotOrder() {
        assertFalse(order.equals(new Object()));
    }

    @Test
    void testGetId() {
        assertEquals(1, order.getId());
    }

    @Test
    void testGetCustomer() {
        assertEquals(1, order.getCustomer());
    }

    @Test
    void testGetPrice() {
        assertEquals(100, order.getPrice());
    }
}