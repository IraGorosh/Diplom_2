package site.nomoreparties.stellarburgers;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.*;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static site.nomoreparties.stellarburgers.steps.UserRegistrationSteps.*;

public class UserRegistrationTest {
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Check successful user creation")
    public void userCanBeCreatedWithValidData() {
        User user = UserGenerator.getRandomUser();

        UserResponse registrationResult = assertCreateReturnsResponse(user, SC_OK)
                .extract()
                .as(UserResponse.class);
        assertThat(registrationResult.isSuccess(), is(true));
        assertThat(registrationResult.getUser().getEmail(), is(user.getEmail()));
        assertThat(registrationResult.getUser().getName(), is(user.getName()));
        assertThat(registrationResult.getAccessToken(), notNullValue());
        assertThat(registrationResult.getRefreshToken(), notNullValue());

        accessToken = registrationResult.getAccessToken();
        userClient.getUserData(accessToken)
                .assertThat()
                .body("user.name", is(user.getName()));

        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check that user can't be created with email that is already in use")
    public void userCanNotBeCreatedWithExistingEmail() {
        User user = UserGenerator.getRandomUser();
        assertCreateReturnsResponse(user, SC_OK);

        User newUser = new User(user.getEmail(), UserGenerator.getRandomString(), UserGenerator.getRandomString());
        UnsuccessfulResponse response = assertCreateReturnsResponse(newUser, SC_FORBIDDEN)
                .extract()
                .as(UnsuccessfulResponse.class);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getMessage(), is("User already exists"));

        assertUserCanLoginAndDelete(user.getEmail(), user.getPassword());
    }

    @Test
    @DisplayName("Check successful user creation with name that is already in use")
    public void userCanBeCreatedWithExistingName() {
        User user = UserGenerator.getRandomUser();
        assertCreateReturnsResponse(user, SC_OK);

        User newUser = new User(UserGenerator.generateEmail(), UserGenerator.getRandomString(), user.getName());
        UserResponse registrationResult = assertCreateReturnsResponse(newUser, SC_OK)
                .extract()
                .as(UserResponse.class);
        accessToken = registrationResult.getAccessToken();
        userClient.getUserData(accessToken)
                .assertThat()
                .body("user.name", is(user.getName()));
        userClient.delete(accessToken);

        assertUserCanLoginAndDelete(user.getEmail(), user.getPassword());
    }

    @Test
    @DisplayName("Check that user can't be created without email")
    public void userCanNotBeCreatedWithoutEmail() {
        assertUserCannotBeCreatedWithoutRequiredParameter(null, UserGenerator.getRandomString(), UserGenerator.getRandomString());
    }

    @Test
    @DisplayName("Check that user can't be created without password")
    public void userCanNotBeCreatedWithoutPassword() {
        assertUserCannotBeCreatedWithoutRequiredParameter(UserGenerator.generateEmail(), null, UserGenerator.getRandomString());
    }

    @Test
    @DisplayName("Check that user can't be created without name")
    public void userCanNotBeCreatedWithoutName() {
        assertUserCannotBeCreatedWithoutRequiredParameter(UserGenerator.generateEmail(), UserGenerator.getRandomString(), null);
    }
}
