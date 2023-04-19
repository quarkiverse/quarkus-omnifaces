/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkiverse.omnifaces.it;

import static java.util.Arrays.asList;

import java.util.Collection;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import org.omnifaces.resourcehandler.WebAppManifest;

@ApplicationScoped
public class ManifestJson extends WebAppManifest {

    @PostConstruct
    public void postConstruct() {
        System.out.println("ManifestJson postConstruct()");
    }

    @Override
    public String getName() {
        return "OmniFaces Quarkus";
    }

    @Override
    public String getShortName() {
        return "OFQ";
    }

    @Override
    public Collection<ImageResource> getIcons() {
        return asList(
                ImageResource.of("layout:img/OmniFaces-icon-192x192.png", Size.SIZE_192),
                ImageResource.of("layout:img/OmniFaces-icon-512x512.png", Size.SIZE_512));
    }

    @Override
    public Display getDisplay() {
        return Display.STANDALONE;
    }

    @Override
    public String getThemeColor() {
        return "#373737";
    }

    @Override
    public String getBackgroundColor() {
        return "#f2f2f2";
    }

    @Override
    protected String getOfflineViewId() {
        return "/notfound.xhtml";
    }

}
