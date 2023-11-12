package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EngineTest {

    private Engine engine;

    @BeforeEach
    void setUp() {
        engine = new Engine();
    }
    @AfterEach
    void tearDown() {
        engine = null;
    }

    @Test
    public void testEngine() {
        Assertions.assertTrue(engine.orderHistory.isEmpty());
    }

    @Test
    public void testGetAverageOrderQuantityByCustomer_HistoryEmpty() {
        Assertions.assertEquals(0, engine.getAverageOrderQuantityByCustomer(1));
    }

    @Test
    public void testGetAverageOrderQuantityByCustomer_NoOrders() {
        int ID_customer = 1;
        int ID_customer2 = 2;

        Order order1 = new Order();
        order1.setCustomer(ID_customer2);
        Order order2 = new Order();
        order2.setCustomer(ID_customer2);

        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);

        Assertions.assertThrows(ArithmeticException.class, () -> {
            engine.getAverageOrderQuantityByCustomer(ID_customer);
        });
    }

    @Test
    public void testGetAverageOrderQuantityByCustomer_SomeOrders() {
        int ID_customer = 1;
        int ID_customer2 = 2;

        Order order1 = new Order();
        order1.setCustomer(ID_customer);
        order1.setQuantity(2);
        Order order2 = new Order();
        order2.setCustomer(ID_customer2);
        order2.setQuantity(4);

        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);

        Assertions.assertEquals(2, engine.getAverageOrderQuantityByCustomer(ID_customer));
    }

    @Test
    public void testGetQuantityPatternByPrice_NoOrders() {
        Assertions.assertEquals(0, engine.getQuantityPatternByPrice(1));
    }

    // TODO: testGetQuantityPatternByPrice_SomeOrders
}