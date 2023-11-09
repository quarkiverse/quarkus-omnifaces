package io.quarkus.omnifaces.runtime;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

public class OmniFacesFeature implements Feature {

    private final static String REASON = "OmniFaces runtime initialization";

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        RuntimeClassInitialization.initializeAtRunTime("org.omnifaces.config.WebXmlSingleton");
    }

    @Override
    public String getDescription() {
        return REASON;
    }
}