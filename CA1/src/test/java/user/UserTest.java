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
    void test_add_normal_credit() throws InvalidCreditRange {
        user.addCredit(100);
        Assertions.assertEquals(100, user.getCredit(), 100);
    }

    @Test
    void reject_add_negative_credit() {
        Assertions.assertThrows(InvalidCreditRange.class, () -> {
            user.addCredit(-50);
        });
    }

    @Test
    void test_withdraw_credit() throws InvalidCreditRange, InsufficientCredit {
        user.addCredit(200);
        user.withdrawCredit(100);
        Assertions.assertEquals(100, user.getCredit());
    }

    @AfterEach
    public void teardown() {
        user = null;
    }
}
