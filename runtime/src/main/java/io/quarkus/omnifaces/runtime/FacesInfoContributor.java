package io.quarkus.omnifaces.runtime;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;

import io.quarkus.info.runtime.spi.InfoContributor;
import io.undertow.Undertow;

public class FacesInfoContributor implements InfoContributor {

    @Override
    public String name() {
        return "Faces";
    }

    @Override
    public Map<String, Object> data() {
        String facesImpl = StringUtils.removeIgnoreCase(StringUtils.removeIgnoreCase(Faces.getImplInfo(), "Core"), "Impl");
        String server = "Undertow " + Undertow.class.getPackage().getImplementationVersion();
        String omniFaces = "OmniFaces: " + StringUtils.defaultIfEmpty(
                org.omnifaces.util.Faces.class.getPackage().getImplementationVersion(), "???");
        LinkedHashMap<String, Object> info = new LinkedHashMap<>(3);
        info.put("faces", facesImpl);
        info.put("server", server);
        info.put("libs", omniFaces);
        return info;
    }
}