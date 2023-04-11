package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import site.nomoreparties.stellarburgers.client.OrderClient;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetUserOrdersTest {
    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private List<String> ingredients;
    private String accessToken;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        ingredients = new ArrayList<>();
        user = UserGenerator.getRandomUser();
        accessToken = userClient.create(user)
                .extract()
                .path("accessToken");
    }

    @After
    public void clearData() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check successful getting a list of user orders")
    public void userCanGetListOfOrders() {
        IngredientList ingredientList = orderClient.getIngredients()
                .extract().as(IngredientList.class);
        ingredients.add(IngredientGenerator.getRandomIngredient(ingredientList.getData()).get_id());

        OrderCreation orderCreation = new OrderCreation(ingredients);
        OrderCreationResponse orderCreationResponse = orderClient.create(orderCreation, accessToken)
                .assertThat().statusCode(SC_OK)
                .extract().as(OrderCreationResponse.class);

        OrderList orderList = orderClient.getUserOrders(accessToken)
                .assertThat().statusCode(SC_OK)
                .extract().as(OrderList.class);

        Order userOrder = orderList.getOrders().get(0);

        assertThat(orderList.isSuccess(), is(true));
        assertThat(orderList.getTotal(), notNullValue());
        assertThat(orderList.getTotalToday(), notNullValue());
        assertThat(userOrder.get_id(), is(orderCreationResponse.getOrder().get_id()));
        assertThat(userOrder.getIngredients(), is(ingredients));
        assertThat(userOrder.getStatus(), is("done"));
        assertThat(userOrder.getName(), is(orderCreationResponse.getName()));
        assertThat(userOrder.getCreatedAt(), notNullValue());
        assertThat(userOrder.getCreatedAt(), notNullValue());
        assertThat(userOrder.getNumber(), is(orderCreationResponse.getOrder().getNumber()));
    }

    @Test
    @DisplayName("Check for not getting a list of user orders without access token")
    public void userCannotGetOrderListWithoutAuthorization() {
        IngredientList ingredientList = orderClient.getIngredients()
                .extract().as(IngredientList.class);
        ingredients.add(IngredientGenerator.getRandomIngredient(ingredientList.getData()).get_id());

        OrderCreation orderCreation = new OrderCreation(ingredients);
        orderClient.create(orderCreation, accessToken)
                .assertThat().statusCode(SC_OK);

        UnsuccessfulResponse response = orderClient.getUserOrders(UserGenerator.getRandomString())
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .extract().as(UnsuccessfulResponse.class);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getMessage(), is("You should be authorised"));
    }
}
