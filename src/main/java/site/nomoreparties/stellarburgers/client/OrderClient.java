package site.nomoreparties.stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import site.nomoreparties.stellarburgers.client.base.BurgerRestClient;
import site.nomoreparties.stellarburgers.model.OrderCreation;

import static io.restassured.RestAssured.given;

public class OrderClient extends BurgerRestClient {
    private static final String ORDERS_URI = BASE_URI + "orders/";
    private static final String INGREDIENTS_URI = BASE_URI + "ingredients/";

    @Step("Get ingredients")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getBaseSpec())
                .header("Content-type", "application/json")
                .when()
                .get(INGREDIENTS_URI)
                .then();
    }

    @Step("Create {orderCreation}")
    public ValidatableResponse create(OrderCreation orderCreation, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(orderCreation)
                .when()
                .post(ORDERS_URI)
                .then();
    }

    @Step("Get user orders")
    public ValidatableResponse getUserOrders(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS_URI)
                .then();
    }
}
