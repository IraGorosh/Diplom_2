package site.nomoreparties.stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import site.nomoreparties.stellarburgers.client.base.BurgerRestClient;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends BurgerRestClient {
    private static final String AUTH_URI = BASE_URI + "auth/";

    @Step("Create {user}")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseSpec())
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(AUTH_URI + "register/")
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(AUTH_URI + "user/")
                .then();
    }

    @Step("Get user data")
    public ValidatableResponse getUserData(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(AUTH_URI + "user/")
                .then();
    }

    @Step("Login as {userCredentials}")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .header("Content-type", "application/json")
                .body(userCredentials)
                .when()
                .post(AUTH_URI + "login/")
                .then();
    }

    @Step("Update data to {user}")
    public ValidatableResponse updateUserData(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(AUTH_URI + "user/")
                .then();
    }
}
