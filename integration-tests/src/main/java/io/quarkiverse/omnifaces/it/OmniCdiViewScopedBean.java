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

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.cdi.viewscope.ViewScopeManager;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import lombok.Data;
import lombok.extern.jbosslog.JBossLog;

@Named
@ViewScoped
@Data
@JBossLog
public class OmniCdiViewScopedBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    @PostConstruct
    public void postConstruct() {
        log.info("OmniFaces postConstruct()");
        message = "Hello from OmniFaces ViewScope!";

        FacesMessage unloadMessage = Faces.removeSessionAttribute("unloadMessage");

        if (unloadMessage != null) {
            Messages.add("cdiViewScopedForm", unloadMessage);
        }

        Messages.addInfo("cdiViewScopedForm", "PostConstruct invoked: {0}", this);
    }

    public void submit() {
        log.info("OmniFaces submit()");
        Messages.addInfo("cdiViewScopedForm", "Submit invoked: {0}", this);
    }

    public String navigate() {
        log.info("OmniFaces navigate()");
        Messages.addInfo("cdiViewScopedForm", "Navigate on POST invoked: {0}", this);
        return Faces.getViewId();
    }

    public void rebuildView() {
        log.info("OmniFaces rebuildView()");
        Messages.addInfo("cdiViewScopedForm", "Rebuild view invoked: {0}", this);
        Faces.setViewRoot(Faces.getViewId());
    }

    @PreDestroy
    public void preDestroy() {
        if (Faces.getContext() != null) { // It can be null during session invalidate!
            log.info("OmniFaces preDestroy");
            if (ViewScopeManager.isUnloadRequest(Faces.getContext())) {
                Faces.setSessionAttribute("unloadMessage",
                        Messages.createInfo("PreDestroy invoked during unload: {0}", this));
            } else {
                Messages.addInfo("cdiViewScopedForm", "PreDestroy invoked during postback: {0}", this);
            }
        }
    }

}