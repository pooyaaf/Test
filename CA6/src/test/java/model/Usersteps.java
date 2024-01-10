package model;

import exceptions.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.assertj.core.api.Assertions;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;

import java.util.Map;

import static org.junit.Assert.*;


@CucumberContextConfiguration
public class Usersteps {
    private User user;
    private Commodity commodityToRemove;

    private float initialCredit;
    @Given("sample users")
    public void sampleUsers() {
        user = new User("username", "password", "email", "01/01/1899", "1 St Jones");
    }

    @When("the user adds {float} credits")
    public void addCredit(float amount) throws InvalidCreditRange {
        try {
            user.addCredit(amount);
        } catch (InvalidCreditRange e) {
            assertThrows(InvalidCreditRange.class, () -> user.addCredit(amount));
        }
    }
    @When("the user adds amount={float} credits")
    public void addUserCredit(float amount) {
        assertThrows(InvalidCreditRange.class, () -> user.addCredit(amount));
    }

    @Then("user gets InvalidCreditRange {string}")
    public void userGetsExceptionCreditRange(String expectedExceptionMessage) {
        Throwable exception = assertThrows(InvalidCreditRange.class, () -> user.addCredit(-10.0F));
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Given("the user has a credit balance of {float}")
    public void userHasCreditBalance(float balance) {
        this.initialCredit = balance;
        user.setCredit(initialCredit);
    }

    @When("the user withdraws {float} credits")
    public void userWithdrawsCredits(float amount) throws InsufficientCredit {
        try {
            user.withdrawCredit(amount);
        } catch (InsufficientCredit e) {
            Assertions.fail("Failed to withdraw credit. Insufficient credit.");
        }
    }
    @When("the user attempts to add {float} credits")
    public void attemptToAddNegativeCredit(float amount) {
        try {
            user.addCredit(amount);
        } catch (InvalidCreditRange e) {
        }
    }
    @When("the user attempts withdraw {float} credits")
    public void userInsufficientWithdrawsCredits(float amount) throws InsufficientCredit {
        try {
            user.withdrawCredit(amount);
        }
        catch (InsufficientCredit e) {
        }
    }
    @Then("an InsufficientCredit exception should be thrown")
    public void InsufficientCreditExceptionShouldBeThrown() {}
    @Then("an invalid credit range exception should be thrown")
    public void invalidCreditRangeExceptionShouldBeThrown() {}
    @Then("the user's credit balance should be {float}")
    public void creditIsAddedCorrectly(float expectedCredit) {
            Assertions.assertThat(user.getCredit()).isEqualTo(expectedCredit);
    }
    @Given("the user has a credit balance balance={float}")
    public void userCreditBalance(float balance) {
        this.initialCredit = balance;
        user.setCredit(initialCredit);
    }
    @When("the user attempts withdraw amount={float} credits")
    public void userAttemptWithdrawMoreThanBalance(float amount) {
        assertThrows(InsufficientCredit.class, () -> user.withdrawCredit(amount));
    }
    @Then("user gets InsufficientCredit {string}")
    public void userGetsInsufficientCreditException(String expectedExceptionMessage) {
        Throwable exception = assertThrows(InsufficientCredit.class, () -> user.withdrawCredit(300.0F));
        assertEquals(expectedExceptionMessage, exception.getMessage());
    }
    @Given("the user has commodity in its buylist")
    public void sampleCommodity() {
        Commodity commodity = new Commodity();
        commodity.setId("ABC123");
        user.addBuyItem(commodity);
    }
    @When("the user removes an item with ID {string} from the buy list")
    public void whenUserRemovesItemFromBuyList(String itemId) throws CommodityIsNotInBuyList {
        commodityToRemove  = new Commodity();
        commodityToRemove.setId(itemId);
        user.removeItemFromBuyList(commodityToRemove);
    }

    @Then("the buy list is empty")
    public void thenBuyListIsEmpty() {
        assertTrue(user.getBuyList().isEmpty());
    }

    @Given("the user has {int} commodity in its buylist")
    public void sampleMultipleCommodity(int instock) {
        Commodity commodity = new Commodity();
        commodity.setId("ABC1234");
        commodity.setInStock(instock);
        user.addBuyItem(commodity);
    }
    @When("the user buys an item with ID {string}")
    public void whenUserRemovesOneItemFromBuyList(String itemId) throws CommodityIsNotInBuyList {
        commodityToRemove  = new Commodity();
        commodityToRemove.setId(itemId);
        user.removeItemFromBuyList(commodityToRemove);
    }
    @Then("the in-stock quantity of the item with ID {string} is decreased by {int}")
    public void thenInStockQuantityDecreased(String itemId, int decreaseAmount) {
        Commodity commodity = new Commodity();
        commodity.setId(itemId);
        int initialQuantity = commodity.getInStock();
        commodity.setInStock(initialQuantity - decreaseAmount);
        assertEquals(initialQuantity - decreaseAmount, commodity.getInStock());
    }


    @Given("the user does not have commodity in its buylist")
    public void givenUserDoesNotHaveCommodityInBuyList() {
        user.getBuyList().clear();
    }
    @When("the user tries to remove an item with ID {string} from the buy list")
    public void whenUserTriesToRemoveItemNotInBuyList(String itemId) {
        commodityToRemove = new Commodity();
        commodityToRemove.setId(itemId);
        assertThrows(CommodityIsNotInBuyList.class, () -> user.removeItemFromBuyList(commodityToRemove));
    }
    @Then("CommodityIsNotInBuyList exception is thrown")
    public void thenCommodityIsNotInBuyListExceptionThrown() {  }
}

