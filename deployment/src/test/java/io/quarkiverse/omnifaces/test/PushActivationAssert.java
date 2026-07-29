package io.quarkiverse.omnifaces.test;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Assertions;

/**
 * Asserts whether the SSE endpoint got registered. It answers an unknown channel with an HTTP 200 close event rather
 * than a container 404, so the status code tells whether it is mapped at all.
 * <p>
 * The web socket endpoint has no such tell, as a plain GET to it yields a 404 either way, so these tests can only
 * observe that a {@code @Push} type does or does not activate SSE. That the default type activates the web socket
 * endpoint is covered by the OmniFaces integration tests.
 */
final class PushActivationAssert {

    private PushActivationAssert() {
        throw new AssertionError();
    }

    static void assertSseEndpointRegistered(URL baseUrl, boolean expected) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "omnifaces.sse/unknownChannel")).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (expected) {
            Assertions.assertEquals(200, response.statusCode(), "SSE endpoint must be registered");
            Assertions.assertTrue(response.body().contains("event: close"), "SSE endpoint must answer with a close event");
        } else {
            Assertions.assertEquals(404, response.statusCode(), "SSE endpoint must not be registered");
        }
    }
}
