package io.quarkiverse.omnifaces.deployment.devui;

import org.omnifaces.config.WebXml;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.ExternalPageBuilder;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.devui.spi.page.PageBuilder;
import io.quarkus.omnifaces.runtime.OmniFacesJsonRPCService;

/**
 * Dev UI card for displaying important details such as the library version.
 */
public class OmnifacesDevUIProcessor {

    private static final String EXTENSION_NAME = "OmniFaces";

    @BuildStep(onlyIf = IsDevelopment.class)
    void createCard(BuildProducer<CardPageBuildItem> cardPageBuildItemBuildProducer) {
        final CardPageBuildItem card = new CardPageBuildItem(EXTENSION_NAME);

        final PageBuilder<ExternalPageBuilder> versionPage = Page.externalPageBuilder("Version")
                .icon("font-awesome-solid:book")
                .url("https://omnifaces.org/")
                .isHtmlContent()
                .staticLabel(WebXml.class.getPackage().getImplementationVersion());
        card.addPage(versionPage);

        final PageBuilder<ExternalPageBuilder> localePage = Page.externalPageBuilder("Locale")
                .icon("font-awesome-solid:location-dot")
                .url("https://omnifaces.org/")
                .isHtmlContent()
                .dynamicLabelJsonRPCMethodName("getLocale");
        card.addPage(localePage);

        final PageBuilder<ExternalPageBuilder> sessionPage = Page.externalPageBuilder("Session Timeout")
                .icon("font-awesome-regular:hourglass")
                .url("https://omnifaces.org/")
                .isHtmlContent()
                .dynamicLabelJsonRPCMethodName("getSessionTimeout");
        card.addPage(sessionPage);

        card.setCustomCard("qwc-omnifaces-card.js");

        cardPageBuildItemBuildProducer.produce(card);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    JsonRPCProvidersBuildItem createJsonRPCService() {
        return new JsonRPCProvidersBuildItem(EXTENSION_NAME, OmniFacesJsonRPCService.class);
    }
}
