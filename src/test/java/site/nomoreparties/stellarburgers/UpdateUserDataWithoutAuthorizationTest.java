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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.UnsuccessfulResponse;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserGenerator;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class UpdateUserDataWithoutAuthorizationTest {
    private UserClient userClient;
    private User user;
    private String accessToken;
    private final String email;
    private final String password;
    private final String name;

    public UpdateUserDataWithoutAuthorizationTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "Parameters for updating user data. Email, password, name: {0}, {1}, {2}")
    public static Object[][] getParameter() {
        return new Object[][]{
                {UserGenerator.generateEmail(), null, null},
                {null, UserGenerator.getRandomString(), null},
                {null, null, UserGenerator.getRandomString()},
        };
    }

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
    @DisplayName("Check that user cannot update data without access token")
    public void userCanNotUpdateDataWithoutAuthorization() {
        User userForUpdate = new User(email, password, name);
        UnsuccessfulResponse response = userClient.updateUserData(userForUpdate, UserGenerator.getRandomString())
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(UnsuccessfulResponse.class);
        assertThat(response.isSuccess(), is(false));
        assertThat(response.getMessage(), is("You should be authorised"));
    }
}
