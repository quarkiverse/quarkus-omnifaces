package io.quarkiverse.omnifaces.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OmnifacesResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/omnifaces")
                .then()
                .statusCode(200)
                .body(is("Hello omnifaces"));
    }
}
