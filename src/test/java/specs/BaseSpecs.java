package specs;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.io.PrintStream;

public class BaseSpecs {

    private static final String API_KEY =
            System.getProperty("REQRES_API_KEY", System.getenv("REQRES_API_KEY"));

    public static RequestSpecification baseRequestSpec() {
        PrintStream ps = System.out;

        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri("https://reqres.in")
                .setBasePath("/api")
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter(ps))
                .addFilter(new ResponseLoggingFilter(ps));

        if (API_KEY != null && !API_KEY.isBlank()) {
            builder.addHeader("x-api-key", API_KEY);
        }

        return builder.build();
    }

    public static ResponseSpecification status200 = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .build();

    public static ResponseSpecification status201 = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .build();

    public static ResponseSpecification status400 = new ResponseSpecBuilder()
            .expectStatusCode(400)
            .build();
}
