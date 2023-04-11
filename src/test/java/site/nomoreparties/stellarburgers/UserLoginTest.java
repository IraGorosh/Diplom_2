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
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.*;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static site.nomoreparties.stellarburgers.steps.UserLoginSteps.assertUserCannotLogInWithIncorrectData;

public class UserLoginTest {
    private UserClient userClient;
    private String accessToken;
    private User user;

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
        user = UserGenerator.getRandomUser();
        userClient.create(user)
                .assertThat()
                .statusCode(SC_OK);
    }

    @After
    public void clearData() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check successful login with valid credentials")
    public void userCanLogInWithValidCredentials() {
        UserCredentials credentials = new UserCredentials(user.getEmail(), user.getPassword());
        UserResponse response = userClient.login(credentials)
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(UserResponse.class);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUser().getEmail(), is(user.getEmail()));
        assertThat(response.getUser().getName(), is(user.getName()));
        assertThat(response.getAccessToken(), notNullValue());
        assertThat(response.getRefreshToken(), notNullValue());

        accessToken = response.getAccessToken();
    }

    @Test
    @DisplayName("Check that user can't login with incorrect email")
    public void userCanNotLogInWithIncorrectEmail() {
        accessToken = assertUserCannotLogInWithIncorrectData(UserGenerator.generateEmail(), user.getPassword(), user);
    }

    @Test
    @DisplayName("Check that user can't login with incorrect password")
    public void userCanNotLogInWithIncorrectPassword() {
        accessToken = assertUserCannotLogInWithIncorrectData(user.getEmail(), UserGenerator.getRandomString(), user);
    }

    @Test
    @DisplayName("Check that user can't login without email")
    public void userCanNotLogInWithoutEmail() {
        accessToken = assertUserCannotLogInWithIncorrectData(null, user.getPassword(), user);
    }

    @Test
    @DisplayName("Check that user can't login without password")
    public void userCanNotLogInWithoutPassword() {
        accessToken = assertUserCannotLogInWithIncorrectData(user.getEmail(), null, user);
    }
}

