package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.client.OrderClient;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderCreationTest {
    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String accessToken;
    private List<String> ingredients;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        ingredients = new ArrayList<>();
    }

    @Test
    @DisplayName("Check successful order creation with access token")
    public void userCanCreateOrderWithAuthorization() {
        userClient = new UserClient();
        user = UserGenerator.getRandomUser();
        accessToken = userClient.create(user).extract().path("accessToken");

        IngredientList ingredientList = orderClient.getIngredients()
                .extract().as(IngredientList.class);
        ingredients.add(IngredientGenerator.getRandomIngredient(ingredientList.getData()).get_id());

        OrderCreation orderCreation = new OrderCreation(ingredients);
        OrderCreationResponse response = orderClient.create(orderCreation, accessToken)
                .assertThat().statusCode(SC_OK)
                .extract().as(OrderCreationResponse.class);

        assertThat(response.isSuccess(), is(true));
        assertThat(response.getName(), notNullValue());
        assertThat(response.getOrder().getIngredients().get(0).get_id(), is(ingredients.get(0)));
        assertThat(response.getOrder().get_id(), notNullValue());
        assertThat(response.getOrder().getOwner().getName(), is(user.getName()));
        assertThat(response.getOrder().getOwner().getEmail(), is(user.getEmail()));
        assertThat(response.getOrder().getStatus(), is("done"));
        assertThat(response.getOrder().getName(), is(response.getName()));
        assertThat(response.getOrder().getCreatedAt(), notNullValue());
        assertThat(response.getOrder().getUpdatedAt(), notNullValue());
        assertThat(response.getOrder().getNumber(), notNullValue());
        assertThat(response.getOrder().getPrice(), notNullValue());

        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check successful order creation without access token")
    public void userCanCreateOrderWithoutAuthorization() {
        IngredientList ingredientList = orderClient.getIngredients()
                .extract().as(IngredientList.class);
        ingredients.add(IngredientGenerator.getRandomIngredient(ingredientList.getData()).get_id());
        OrderCreation orderCreation = new OrderCreation(ingredients);
        OrderCreationResponse response = orderClient.create(orderCreation, UserGenerator.getRandomString())
                .assertThat().statusCode(SC_OK)
                .extract().as(OrderCreationResponse.class);

        assertThat(response.isSuccess(), is(true));
        assertThat(response.getName(), notNullValue());
        assertThat(response.getOrder().getNumber(), notNullValue());
    }

    @Test
    @DisplayName("Check that order cannot be created with invalid ingredient id")
    public void userCannotCreateOrderWithInvalidIngredientId() {
        ingredients.add(IngredientGenerator.getRandomId());
        OrderCreation orderCreation = new OrderCreation(ingredients);
        orderClient.create(orderCreation, UserGenerator.getRandomString())
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Check that order cannot be created without ingredients")
    public void userCannotCreateOrderWithoutIngredients() {
        OrderCreation orderCreation = new OrderCreation(ingredients);
        UnsuccessfulResponse response = orderClient.create(orderCreation, UserGenerator.getRandomString())
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .extract().as(UnsuccessfulResponse.class);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getMessage(), is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check successful order creation with multiple ingredients")
    public void userCanCreateOrderWithAFewIngredients() {
        IngredientList ingredientList = orderClient.getIngredients()
                .extract().as(IngredientList.class);
        for (int i = 0; i < IngredientGenerator.getRandomAmount(ingredientList.getData()); i++) {
            ingredients.add(IngredientGenerator.getRandomIngredient(ingredientList.getData()).get_id());
        }
        OrderCreation orderCreation = new OrderCreation(ingredients);
        OrderCreationResponse response = orderClient.create(orderCreation, UserGenerator.getRandomString())
                .assertThat().statusCode(SC_OK)
                .extract().as(OrderCreationResponse.class);

        assertThat(response.isSuccess(), is(true));
        assertThat(response.getName(), notNullValue());
        assertThat(response.getOrder().getNumber(), notNullValue());
    }
}
