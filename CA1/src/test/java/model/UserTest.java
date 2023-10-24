package model;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class UserTest {
    private User user;
    @BeforeEach
    void setUp() {
        user = new User("username", "password", "email", "01/01/1899", "1 St Jones");
    }

    @Test
    void test_user_constructor() {
        Assertions.assertEquals("username", user.getUsername());
        Assertions.assertEquals("password", user.getPassword());
        Assertions.assertEquals("email", user.getEmail());
        Assertions.assertEquals("01/01/1899", user.getBirthDate());
        Assertions.assertEquals("1 St Jones", user.getAddress());
    }

    @Test
    void test_add_credit() throws InvalidCreditRange {
        user.addCredit(100);
        Assertions.assertEquals(100, user.getCredit(), 100);
    }

    @Test
    void reject_negative_credit() {
        Assertions.assertThrows(InvalidCreditRange.class, () -> user.addCredit(-50));
    }

    @Test
    void test_withdraw_credit() throws InvalidCreditRange, InsufficientCredit {
        user.addCredit(200);
        user.withdrawCredit(100);
        Assertions.assertEquals(100, user.getCredit());
    }

    @Test
    void reject_withdraw_more_credit_than_available() throws InvalidCreditRange {
        user.addCredit(100);
        Assertions.assertThrows(InsufficientCredit.class, () -> user.withdrawCredit(200));
    }

    @Test
    void test_add_item_to_buyList(){
        Commodity commodity = new Commodity();
        commodity.setId("1");
        user.addBuyItem(commodity);
        Assertions.assertEquals(1, user.getBuyList().get("1"));

    }

    @Test
    void test_add_item_to_buyList_without_id(){
        Commodity commodity = new Commodity();
        user.addBuyItem(commodity);
        Assertions.assertNull(user.getBuyList().get("null"));
    }


    @Test
    void test_add_purchasedItem() {
        user.addPurchasedItem("commodityId", 20);
        Assertions.assertEquals(20, user.getPurchasedList().get("commodityId"));
    }

    @Test
    void test_remove_item_from_buyList() throws CommodityIsNotInBuyList {
        Commodity commodity = new Commodity();
        commodity.setId("commodityId");
        user.addBuyItem(commodity);
        user.removeItemFromBuyList(commodity);
        Assertions.assertNull(user.getBuyList().get("commodityId"));
    }

    @Test
    void reject_removing_non_existing_item_from_buyList(){
        Commodity commodity = new Commodity();
        commodity.setId("commodityId");
        Assertions.assertThrows(CommodityIsNotInBuyList.class, () -> user.removeItemFromBuyList(commodity));
    }

    @AfterEach
    public void teardown() {
        user = null;
    }
}
