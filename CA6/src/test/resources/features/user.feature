Feature: User

  Background:
    Given sample users

  Scenario: User adds credit to their account
    When the user adds 50.0 credits
    Then the user's credit balance should be 50.0

  Scenario Outline: user can't add negative credit to its account
    When the user adds amount=<amount> credits
    Then user gets InvalidCreditRange <exception>
    Examples:
      | amount | exception |
      | -10.0 | "Credit value must be a positive float" |

  Scenario: User adds negative credit to their account
    When the user attempts to add -30.0 credits
    Then an invalid credit range exception should be thrown

  Scenario: User withdraws credits from their account
    Given the user has a credit balance of 100.0
    When the user withdraws 30.0 credits
    Then the user's credit balance should be 70.0

  Scenario Outline: User attempts withdraws credits more than what it has
    Given the user has a credit balance balance=<balance>
    When the user attempts withdraw amount=<amount> credits
    Then user gets InsufficientCredit <exception>
    Examples:
      | balance | amount | exception |
      | 200     | 300    | "Credit is insufficient." |


  Scenario: User removes an item from the buy list
    Given the user has commodity in its buylist
    When the user removes an item with ID "ABC123" from the buy list
    Then the buy list is empty

  Scenario: User buys an item and updates the in-stock quantity
    Given the user has 2 commodity in its buylist
    When the user buys an item with ID "ABC1234"
    Then the in-stock quantity of the item with ID "ABC1234" is decreased by 1

  Scenario: User tries to remove an item that is not in the buy list
    Given the user does not have commodity in its buylist
    When the user tries to remove an item with ID "XYZ789" from the buy list
    Then CommodityIsNotInBuyList exception is thrown