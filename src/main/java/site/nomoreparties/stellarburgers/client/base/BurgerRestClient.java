package site.nomoreparties.stellarburgers.client.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BurgerRestClient {
    protected static final String BASE_URI = "https://stellarburgers.nomoreparties.site/api/";

    protected RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URI)
                .build();
    }
}
