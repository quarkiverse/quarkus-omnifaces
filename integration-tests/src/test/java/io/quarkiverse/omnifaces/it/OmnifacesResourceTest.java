package io.quarkiverse.omnifaces.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OmnifacesResourceTest {

    @TestHTTPResource
    URL url;

    private static WebClient webClient;

    @BeforeAll
    public static void initWebClient() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    }

    @AfterAll
    public static void closeWebClient() {
        if (webClient != null) {
            webClient.close();
        }
    }

    // TODO: Convert to Selenium Test
    // @Test
    public void shouldOpenIndexPage() throws Exception {
        final HtmlPage page = webClient.getPage(url + "/index.xhtml");
        final HtmlSpan message = (HtmlSpan) page.getElementById("message");
        assertThat(message).isNotNull();
        assertThat(message.getTextContent()).isEqualTo("Hello from OmniFaces ViewScope!");
    }
}
