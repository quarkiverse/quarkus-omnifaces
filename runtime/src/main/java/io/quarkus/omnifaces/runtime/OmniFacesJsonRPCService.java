package io.quarkus.omnifaces.runtime;

import jakarta.annotation.PostConstruct;

import org.omnifaces.config.WebXml;
import org.omnifaces.util.Faces;

/**
 * Provide runtime data for JSON-RPC Endpoint.
 */
public class OmniFacesJsonRPCService {

    @PostConstruct
    void init() {

    }

    public String getLocale() {
        return Faces.getLocale().toLanguageTag();
    }

    public Integer getSessionTimeout() {
        return WebXml.instance().getSessionTimeout();
    }

}
