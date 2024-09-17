package io.quarkus.omnifaces.runtime;

import jakarta.enterprise.inject.spi.InjectionPoint;

import org.omnifaces.cdi.param.ParamProducer;
import org.omnifaces.cdi.param.ParamValue;

import io.quarkus.arc.BeanCreator;
import io.quarkus.arc.SyntheticCreationalContext;

/**
 * A bean creator for synthetic beans annotated with @Param in Quarkus.
 * This class delegates the actual creation to OmniFaces' ParamProducer.
 */
public class ParamBeanCreator implements BeanCreator<Object> {

    /**
     * Creates a bean instance for the given synthetic context.
     *
     * @param context The synthetic creational context for the bean.
     * @return The created bean instance.
     * @throws IllegalStateException if no current injection point is found.
     */
    @Override
    public Object create(SyntheticCreationalContext<Object> context) {
        InjectionPoint injectionPoint = context.getInjectedReference(InjectionPoint.class);
        if (injectionPoint == null) {
            throw new IllegalStateException("@Param no current injection point found");
        }
        // delegate to the original producer
        ParamValue<?> paramValue = new ParamProducer().produce(injectionPoint);
        return paramValue.getValue();
    }
}
