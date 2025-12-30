package io.quarkiverse.omnifaces.deployment.devui;

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

    @BuildStep(onlyIf = IsDevelopment.class)
    void createCard(BuildProducer<CardPageBuildItem> cardPageBuildItemBuildProducer) {
        final CardPageBuildItem card = new CardPageBuildItem();

        card.addLibraryVersion("org.omnifaces", "omnifaces", "OmniFaces",
                "https://omnifaces.org/");

        final PageBuilder<ExternalPageBuilder> localePage = Page.externalPageBuilder("Locale")
                .icon("font-awesome-solid:location-dot")
                .url("https://omnifaces.org/")
                .doNotEmbed()
                .dynamicLabelJsonRPCMethodName("getLocale");
        card.addPage(localePage);

        final PageBuilder<ExternalPageBuilder> sessionPage = Page.externalPageBuilder("Session Timeout")
                .icon("font-awesome-regular:hourglass")
                .url("https://omnifaces.org/")
                .doNotEmbed()
                .dynamicLabelJsonRPCMethodName("getSessionTimeout");
        card.addPage(sessionPage);

        cardPageBuildItemBuildProducer.produce(card);
    }

    @BuildStep
    JsonRPCProvidersBuildItem createJsonRPCService() {
        return new JsonRPCProvidersBuildItem(OmniFacesJsonRPCService.class);
    }
}
