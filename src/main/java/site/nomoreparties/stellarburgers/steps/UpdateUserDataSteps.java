package site.nomoreparties.stellarburgers.steps;

import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserResponse;

import static org.apache.http.HttpStatus.SC_OK;

public class UpdateUserDataSteps {

    public static UserResponse assertUpdateUserDataReturnsResponse(String email, String password, String name, String accessToken) {
        User userForUpdate = new User(email, password, name);
        UserClient userClient = new UserClient();
        return userClient.updateUserData(userForUpdate, accessToken)
                .assertThat()
                .statusCode(SC_OK)
                .extract()
                .as(UserResponse.class);
    }
}
