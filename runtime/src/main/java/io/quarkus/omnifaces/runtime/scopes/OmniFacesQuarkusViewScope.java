/*
 * Copyright 2019 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkus.omnifaces.runtime.scopes;

import static org.omnifaces.util.Beans.getReference;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.omnifaces.cdi.viewscope.ViewScopeContext;
import org.omnifaces.cdi.viewscope.ViewScopeManager;

import io.quarkus.arc.InjectableContext;

public class OmniFacesQuarkusViewScope implements InjectableContext {

    private ViewScopeContext wrapped;

    public OmniFacesQuarkusViewScope() {
        wrapped = new ViewScopeContext();
    }

    @Override
    public void destroy() {
        ViewScopeManager reference = getReference(ViewScopeManager.class);
        reference.preDestroyView();
    }

    @Override
    public ContextState getState() {
        return null;
    }

    @Override
    public void destroy(Contextual<?> contextual) {
        this.destroy();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return wrapped.getScope();
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> cc) {
        return wrapped.get(contextual, cc);
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return wrapped.get(contextual);
    }

    @Override
    public boolean isActive() {
        return wrapped.isActive();
    }

}
