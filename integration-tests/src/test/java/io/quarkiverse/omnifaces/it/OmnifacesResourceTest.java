package io.quarkiverse.omnifaces.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;

import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithPlaywright
public class OmnifacesResourceTest {

    @InjectPlaywright
    BrowserContext context;

    @TestHTTPResource("/index.xhtml")
    URL index;

    @Test
    public void shouldOpenIndexPage() throws Exception {
        final Page page = context.newPage();
        Response response = page.navigate(index.toString());
        Assertions.assertEquals("OK", response.statusText());

        page.waitForLoadState();

        String title = page.title();
        Assertions.assertEquals("Quarkiverse OmniFaces", title);

        Locator message = page.locator("#message");
        assertThat(message).isNotNull();
        assertThat(message.innerText()).isEqualTo("Hello from OmniFaces ViewScope!");
    }
}
