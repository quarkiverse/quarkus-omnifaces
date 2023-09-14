package io.quarkus.omnifaces.runtime;

import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;

public class OmniFacesFeature implements Feature {

    private final static String REASON = "OmniFaces runtime initialization";

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        final RuntimeClassInitializationSupport runtimeInit = ImageSingletons.lookup(RuntimeClassInitializationSupport.class);
        runtimeInit.initializeAtRunTime("org.omnifaces.config.WebXmlSingleton", REASON);
    }

    @Override
    public String getDescription() {
        return REASON;
    }
}