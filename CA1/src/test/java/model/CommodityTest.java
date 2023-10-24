package model;

import exceptions.NotInStock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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


//   updateInStock tests
    @ParameterizedTest
    @CsvSource({
            "2, 12",
            "3, 20",
            "10, 1",
            "0, 10",
            "10, 0",
    })
    void test_update_inStock_WhenNewInStockIsPos(int InStock, int amount) throws NotInStock {
        commodity.setInStock(InStock);
        commodity.updateInStock(amount);
        Assertions.assertEquals(InStock + amount, commodity.getInStock());
    }

    @ParameterizedTest
    @CsvSource({
            "2, -12",
            "3, -20",
            "10, -11",
            "0, -10",
            "10, -100"
    })
    void test_update_inStock_WhenNewInStockIsNeg(int InStock, int amount){
        commodity.setInStock(InStock);
        Assertions.assertThrows(NotInStock.class, () -> commodity.updateInStock(amount));
    }

    @ParameterizedTest
    @CsvSource({
            "2, -2",
            "3, -3",
            "10, -10",
            "0, 0",
            "10, -10"
    })
    void test_update_inStock_WhenNewInStockIsZero(int InStock, int amount) throws NotInStock {
        commodity.setInStock(InStock);
        commodity.updateInStock(amount);
        Assertions.assertEquals(0, commodity.getInStock());
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
