package model;

import exceptions.NotInStock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        commodity.setInitRate(8);
        // Add categories
        ArrayList<String> categories = new ArrayList<>();
        categories.add("tech");
        categories.add("phone");
        commodity.setCategories(categories);
        // Add rates
        Map<String, Integer> userRate = new HashMap<>();
        userRate.put("user1", 6);
        userRate.put("user2", 10);
        commodity.setUserRate(userRate);
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


//  addRate tests
    @ParameterizedTest
    @CsvSource({
            "user4, 1",
            "user5, 2",
            "user6, 3",
            "user7, 4",
            "user8, 5",
            "user9, 6",
            "user10, 7",
            "user11, 8",
            "user12, 9",
            "user13, 10"
    })
    void test_add_rate_HappyPath(String username, int score) {
        commodity.addRate(username, score);
        float expected = (float) (score + 24) / 4;
        Assertions.assertEquals(expected, commodity.getRating());
    }

    @ParameterizedTest
    @CsvSource({
            "user4, 14",
            "user5, 13",
            "user6, 12",
            "user7, 11",
            "user4, 0",
            "user5, -1",
            "user6, -2",
            "user7, -3",
            "user8, -200"
    })
    void test_add_rate_SadPath(String username, int score) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> commodity.addRate(username, score));
    }


    @AfterEach
    void tearDown() {
        commodity = null;
    }
}
