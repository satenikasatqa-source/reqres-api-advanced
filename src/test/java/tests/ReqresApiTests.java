package tests;

import com.github.javafaker.Faker;
import helpers.UserApiSteps;
import helpers.Waiter;
import io.restassured.response.Response;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.ErrorResponse;
import models.LoginRequest;
import models.LoginSuccessResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import specs.TestBaseAPI;

import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static specs.BaseSpecs.*;

public class ReqresApiTests extends TestBaseAPI {

    UserApiSteps userApi = new UserApiSteps();

    @Test
    @Tag("api")
    @Tag("smoke")
    @DisplayName("Создание пользователя через steps: возвращает 201 и корректные поля")
    void createUser_withStepsLayer() {

        CreateUserRequest req = new CreateUserRequest("Saten", "QA");

        CreateUserResponse resp = userApi.createUser(req);

        assertThat(resp.getName()).isEqualTo("Saten");
        assertThat(resp.getJob()).isEqualTo("QA");
        assertThat(resp.getId()).isNotBlank();
        assertThat(resp.getCreatedAt()).isNotBlank();
    }

    @ParameterizedTest(name = "Создание пользователя: name={0}, job={1}")
    @CsvSource({
            "Saten, QA",
            "Ani, Developer",
            "Arman, Manager"
    })
    @Tag("api")
    @Tag("regression")
    @DisplayName("Создание пользователя (параметризованный тест)")
    void createUser_parameterized_shouldReturn201(String name, String job) {

        CreateUserRequest req = new CreateUserRequest(name, job);

        CreateUserResponse resp =
                given()
                        .body(req)
                        .when()
                        .post("/users")
                        .then()
                        .spec(status201)
                        .extract().as(CreateUserResponse.class);

        assertThat(resp.getName()).isEqualTo(name);
        assertThat(resp.getJob()).isEqualTo(job);
        assertThat(resp.getId()).isNotBlank();
        assertThat(resp.getCreatedAt()).isNotBlank();
    }

    @Test
    @Tag("api")
    @Tag("regression")
    @DisplayName("Создание пользователя с Faker: случайные данные возвращаются в ответе")
    void createUser_withFaker_shouldReturn201() {

        Faker faker = new Faker();

        String name = faker.name().firstName();
        String job = faker.job().title();

        CreateUserRequest req = new CreateUserRequest(name, job);

        CreateUserResponse resp = userApi.createUser(req);

        assertThat(resp.getName()).isEqualTo(name);
        assertThat(resp.getJob()).isEqualTo(job);
        assertThat(resp.getId()).isNotBlank();
        assertThat(resp.getCreatedAt()).isNotBlank();
    }

    @Test
    @Tag("api")
    @Tag("regression")
    @DisplayName("Создание пользователя DTO: возвращает 201 и корректные поля")
    void createUser_shouldReturn201_andReturnNameAndJob_dto() {

        CreateUserRequest body = new CreateUserRequest("Saten", "QA");

        CreateUserResponse response =
                given()
                        .body(body)
                        .when()
                        .post("/users")
                        .then()
                        .spec(status201)
                        .extract().as(CreateUserResponse.class);

        assertThat(response.getName()).isEqualTo("Saten");
        assertThat(response.getJob()).isEqualTo("QA");
        assertThat(response.getId()).isNotBlank();
        assertThat(response.getCreatedAt()).isNotBlank();
    }

    @Test
    @Tag("api")
    @Tag("smoke")
    @DisplayName("Логин: успешный вход возвращает токен")
    void login_success_shouldReturn200_andToken() {

        LoginRequest body = new LoginRequest("eve.holt@reqres.in", "cityslicka");

        LoginSuccessResponse response =
                given()
                        .body(body)
                        .when()
                        .post("/login")
                        .then()
                        .spec(status200)
                        .extract().as(LoginSuccessResponse.class);

        assertThat(response.getToken()).isNotBlank();
    }

    @Test
    @Tag("api")
    @Tag("smoke")
    @DisplayName("Получение пользователей: страница 2 не пустая")
    void getUsers_page2_shouldReturn200_andNotEmptyData() {

        given()
                .queryParam("page", 2)
                .when()
                .get("/users")
                .then()
                .spec(status200)
                .body("data", not(empty()));
    }

    @Test
    @Tag("api")
    @Tag("smoke")
    @DisplayName("Получение пользователя: id=2 возвращает корректные данные")
    void getSingleUser_shouldReturn200_andCorrectId() {

        given()
                .when()
                .get("/users/2")
                .then()
                .spec(status200)
                .body("data.id", equalTo(2))
                .body("data.email", notNullValue());
    }

    @Test
    @Tag("api")
    @Tag("negative")
    @DisplayName("Логин: без пароля возвращает ошибку 400")
    void login_withMissingPassword_shouldReturn400() {

        LoginRequest body = new LoginRequest("eve.holt@reqres.in", null);

        ErrorResponse response =
                given()
                        .body(body)
                        .when()
                        .post("/login")
                        .then()
                        .spec(status400)
                        .extract().as(ErrorResponse.class);

        assertThat(response.getError()).isNotBlank();
    }

    @Test
    @Tag("api")
    @Tag("demo")
    @Tag("flaky")
    @DisplayName("Polling demo: ожидание успешного ответа пользователей")
    void polling_example_shouldWaitUntilUsersReturned() {

        Response resp = Waiter.waitFor(
                () -> given()
                        .queryParam("delay", 3)
                        .queryParam("page", 2)
                        .when()
                        .get("/users"),
                r -> r.statusCode() == 200,
                Duration.ofSeconds(10),
                Duration.ofSeconds(1)
        );

        assertThat(resp.statusCode()).isEqualTo(200);
        resp.then().body("data", not(empty()));
    }
}
