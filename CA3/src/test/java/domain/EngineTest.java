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

        Assertions.assertThrows(ArithmeticException.class, () -> engine.getAverageOrderQuantityByCustomer(ID_customer));
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

    @Test
    public void testGetQuantityPatternByPrice_WithoutSamePrice() {
        int price = 15;

        Order order1 = new Order();
        order1.setId(1);
        order1.setPrice(price);
        order1.setQuantity(2);

        Order order2 = new Order();
        order2.setId(2);
        order2.setPrice(10);
        order2.setQuantity(4);

        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);

        Assertions.assertEquals(0, engine.getQuantityPatternByPrice(price));
    }

    @Test
    public void testGetQuantityPatternByPrice_Wit1SamePrice() {
        int price = 15;

        Order order1 = new Order();
        order1.setId(1);
        order1.setPrice(price);
        order1.setQuantity(2);

        Order order2 = new Order();
        order2.setId(2);
        order2.setPrice(price);
        order2.setQuantity(4);

        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);

        int expected = order2.getQuantity() - order1.getQuantity();
        Assertions.assertEquals(expected, engine.getQuantityPatternByPrice(price));
    }

    @Test
    public void testGetQuantityPatternByPrice_WithMultipleSamePrice_SameQuantity() {
        int price = 15;

        Order order1 = new Order();
        order1.setId(1);
        order1.setPrice(price);
        order1.setQuantity(2);

        Order order2 = new Order();
        order2.setId(2);
        order2.setPrice(price);
        order2.setQuantity(4);

        Order order3 = new Order();
        order3.setId(3);
        order3.setPrice(price);
        order3.setQuantity(6);

        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);

        int expected = order2.getQuantity() - order1.getQuantity();
        Assertions.assertEquals(expected, engine.getQuantityPatternByPrice(price));
    }

    @Test
    public void testGetQuantityPatternByPrice_WithMultipleSamePrice_DifferentQuantity() {
        int price = 15;

        Order order1 = new Order();
        order1.setId(1);
        order1.setPrice(price);
        order1.setQuantity(2);

        Order order2 = new Order();
        order2.setId(2);
        order2.setPrice(price);
        order2.setQuantity(4);

        Order order3 = new Order();
        order3.setId(3);
        order3.setPrice(price);
        order3.setQuantity(5);

        engine.orderHistory.add(order1);
        engine.orderHistory.add(order2);
        engine.orderHistory.add(order3);

        Assertions.assertEquals(0, engine.getQuantityPatternByPrice(price));
    }

}