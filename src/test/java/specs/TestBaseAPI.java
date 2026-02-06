package specs;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static specs.BaseSpecs.baseRequestSpec;

public class TestBaseAPI {

    @BeforeAll
    static void beforeAll() {
        RestAssured.requestSpecification = baseRequestSpec();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}

