package specs;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import static specs.BaseSpecs.baseRequestSpec;

public class TestBaseAPI {

    @BeforeAll
    static void beforeAll() {

        String key = System.getProperty("REQRES_API_KEY", System.getenv("REQRES_API_KEY"));
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("REQRES_API_KEY is not set");
        }
        RestAssured.requestSpecification = baseRequestSpec();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
