package io.quarkus.omnifaces.runtime;

import jakarta.enterprise.inject.spi.InjectionPoint;

import org.omnifaces.cdi.param.ParamProducer;
import org.omnifaces.cdi.param.ParamValue;

import io.quarkus.arc.BeanCreator;
import io.quarkus.arc.SyntheticCreationalContext;

public class ParamBeanCreator implements BeanCreator<Object> {

    @Override
    public Object create(SyntheticCreationalContext<Object> context) {
        InjectionPoint injectionPoint = context.getInjectedReference(InjectionPoint.class);
        if (injectionPoint == null) {
            throw new IllegalStateException("No current injection point found");
        }
        // delegate to the original producer
        ParamValue<?> paramValue = new ParamProducer().produce(injectionPoint);
        return paramValue.getValue();
    }
}
