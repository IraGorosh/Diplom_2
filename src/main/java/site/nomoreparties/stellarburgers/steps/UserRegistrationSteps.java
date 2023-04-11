package site.nomoreparties.stellarburgers.steps;

import io.restassured.response.ValidatableResponse;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.UnsuccessfulResponse;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserCredentials;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserRegistrationSteps {
    public static ValidatableResponse assertCreateReturnsResponse(User user, int httpStatus) {
        UserClient userClient = new UserClient();
        return userClient
                .create(user)
                .assertThat()
                .statusCode(httpStatus);
    }

    public static void assertUserCannotBeCreatedWithoutRequiredParameter(String email, String password, String name) {
        User user = new User(email, password, name);
        UnsuccessfulResponse response = assertCreateReturnsResponse(user, SC_FORBIDDEN)
                .extract()
                .as(UnsuccessfulResponse.class);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getMessage(), is("Email, password and name are required fields"));
    }

    public static void assertUserCanLoginAndDelete(String email, String password) {
        UserClient userClient = new UserClient();
        UserCredentials credentials = new UserCredentials(email, password);
        String accessToken = userClient.login(credentials)
                .extract()
                .path("accessToken");
        userClient.delete(accessToken);
    }
}
