package user;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.User;
import org.junit.jupiter.api.Assertions;

public class UserTest {
    private User user;
    @BeforeEach
    void setUp() {
        user = new User("username", "password", "email", "01/01/1899", "1 St Jones");
    }
    @Test
    void testAddCredit() throws InvalidCreditRange {
        user.addCredit(100);
        Assertions.assertEquals(100, user.getCredit(), 100);
    }
    // AfterEach
}
