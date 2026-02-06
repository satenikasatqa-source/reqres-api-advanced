package helpers;

import io.qameta.allure.Step;
import models.CreateUserRequest;
import models.CreateUserResponse;

import static io.restassured.RestAssured.given;
import static specs.BaseSpecs.status201;

public class UserApiSteps {

    @Step("Create user with name={req.name} and job={req.job}")
    public CreateUserResponse createUser(CreateUserRequest req) {

        return given()
                .body(req)
                .when()
                .post("/users")
                .then()
                .spec(status201)
                .extract().as(CreateUserResponse.class);
    }
}
