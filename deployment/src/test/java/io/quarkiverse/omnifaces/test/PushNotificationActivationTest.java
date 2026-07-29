package io.quarkiverse.omnifaces.test;

import java.net.URL;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;

import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.common.http.TestHTTPResource;

/**
 * Verifies that a {@code @Push} injection point of type NOTIFICATION activates the associated endpoint, even though ArC does
 * not
 * run the CDI portable extension which OmniFaces itself relies on to detect them.
 */
public class PushNotificationActivationTest {

    @ApplicationScoped
    public static class PushBean {

        @Inject
        @Push(type = Push.Type.NOTIFICATION)
        PushContext push;
    }

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(
                    () -> ShrinkWrap.create(JavaArchive.class).addClasses(PushBean.class, PushActivationAssert.class));

    @TestHTTPResource
    URL baseUrl;

    @Test
    public void testSseEndpointRegistration() throws Exception {
        PushActivationAssert.assertSseEndpointRegistered(baseUrl, true);
    }
}
