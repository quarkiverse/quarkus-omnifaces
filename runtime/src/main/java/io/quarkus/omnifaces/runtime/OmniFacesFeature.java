package io.quarkus.omnifaces.runtime;

import org.graalvm.nativeimage.hosted.Feature;

public class OmniFacesFeature implements Feature {

    private final static String REASON = "OmniFaces runtime initialization";

    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
    }

    @Override
    public String getDescription() {
        return REASON;
    }
}
