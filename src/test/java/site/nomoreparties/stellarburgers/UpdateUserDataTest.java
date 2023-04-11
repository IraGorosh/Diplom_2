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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static site.nomoreparties.stellarburgers.steps.UpdateUserDataSteps.assertUpdateUserDataReturnsResponse;

public class UpdateUserDataTest {
    private UserClient userClient;
    private User user;
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
    @DisplayName("Check successful updating user email")
    public void userCanUpdateEmailSuccessfully() {
        String email = UserGenerator.generateEmail();
        UserResponse response = assertUpdateUserDataReturnsResponse(email, null, null, accessToken);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUser().getEmail(), is(email));
        assertThat(response.getUser().getName(), is(user.getName()));
    }

    @Test
    @DisplayName("Check successful updating password")
    public void userCanUpdatePasswordSuccessfully() {
        String password = UserGenerator.getRandomString();
        UserResponse response = assertUpdateUserDataReturnsResponse(null, password, null, accessToken);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUser().getEmail(), is(user.getEmail()));
        assertThat(response.getUser().getName(), is(user.getName()));

        UserCredentials credentials = new UserCredentials(user.getEmail(), password);
        accessToken = userClient.login(credentials)
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Check successful updating user name")
    public void userCanUpdateNameSuccessfully() {
        String name = UserGenerator.getRandomString();
        UserResponse response = assertUpdateUserDataReturnsResponse(null, null, name, accessToken);
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getUser().getEmail(), is(user.getEmail()));
        assertThat(response.getUser().getName(), is(name));
    }

    @Test
    @DisplayName("Check that impossible to update email that is already in use")
    public void userCanNotUpdateEmailThatIsAlreadyInUse() {
        User secondUser = UserGenerator.getRandomUser();
        String accessTokenSecondUser = userClient.create(secondUser)
                .extract()
                .path("accessToken");

        User userForUpdate = new User(user.getEmail(), null, null);
        UnsuccessfulResponse response = userClient.updateUserData(userForUpdate, accessTokenSecondUser)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .extract()
                .as(UnsuccessfulResponse.class);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getMessage(), is("User with such email already exists"));

        userClient.delete(accessTokenSecondUser);
    }
}
