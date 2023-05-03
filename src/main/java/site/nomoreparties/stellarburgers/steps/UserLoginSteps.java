package site.nomoreparties.stellarburgers.steps;

import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.UnsuccessfulResponse;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserCredentials;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserLoginSteps {
    public static String assertUserCannotLogInWithIncorrectData(String email, String password, User user) {
        UserCredentials credentials = new UserCredentials(email, password);
        UserClient userClient = new UserClient();
        UnsuccessfulResponse response = userClient.login(credentials)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(UnsuccessfulResponse.class);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getMessage(), is("email or password are incorrect"));

        UserCredentials rightCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        return userClient.login(rightCredentials)
                .extract()
                .path("accessToken");
    }
}
