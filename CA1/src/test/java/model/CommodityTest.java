package model;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import exceptions.NotInStock;
import model.Commodity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.User;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class CommodityTest {
    private Commodity commodity;

    @BeforeEach
    void setUp() {
        commodity = new Commodity();
        commodity.setId("commodityId");
        commodity.setName("Phone 1");
        commodity.setProviderId("Samsung");
        commodity.setPrice(100);
        commodity.setInStock(10);
        commodity.setImage("image");
        // Add categories
        ArrayList<String> categories = new ArrayList<>();
        categories.add("tech");
        categories.add("phone");
        commodity.setCategories(categories);
    }

    @Test
    void test_update_inStock() throws NotInStock {
        commodity.updateInStock(5);
        Assertions.assertEquals(15, commodity.getInStock());
    }
    @Test
    void reject_update_inStock_negative() {
        Assertions.assertThrows(NotInStock.class, () -> {
            commodity.updateInStock(-15);
        });
    }

    @Test
    void test_add_rate() {
        commodity.addRate("user1", 4);
        Assertions.assertEquals(4, commodity.getRating());
    }
    @Test
    void test_calc_rate_formula() {
        commodity.addRate("user1", 4);
        commodity.addRate("user2", 5);
        Assertions.assertEquals(4.5, commodity.getRating());
    }
    @AfterEach
    void tearDown() {
        commodity = null;
    }
}
