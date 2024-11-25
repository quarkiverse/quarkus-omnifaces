package io.quarkiverse.omnifaces.deployment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.InjectionPoint;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;
import org.jboss.logging.Logger;
import org.omnifaces.cdi.ContextParam;
import org.omnifaces.cdi.Cookie;
import org.omnifaces.cdi.Eager;
import org.omnifaces.cdi.GraphicImageBean;
import org.omnifaces.cdi.Param;
import org.omnifaces.cdi.PostScriptParam;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.Startup;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.cdi.converter.ConverterManager;
import org.omnifaces.cdi.eager.EagerBeansRepository;
import org.omnifaces.cdi.push.SocketChannelManager;
import org.omnifaces.cdi.push.SocketPushContextProducer;
import org.omnifaces.cdi.validator.ValidatorManager;
import org.omnifaces.cdi.viewscope.ViewScopeManager;
import org.omnifaces.resourcehandler.CombinedResourceHandler;
import org.omnifaces.resourcehandler.WebAppManifest;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.deployment.BeanDiscoveryFinishedBuildItem;
import io.quarkus.arc.deployment.ContextRegistrationPhaseBuildItem;
import io.quarkus.arc.deployment.ContextRegistrationPhaseBuildItem.ContextConfiguratorBuildItem;
import io.quarkus.arc.deployment.CustomScopeBuildItem;
import io.quarkus.arc.deployment.KnownCompatibleBeanArchiveBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.arc.processor.InjectionPointInfo;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AdditionalApplicationArchiveMarkerBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.NativeImageFeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.steps.NativeOrNativeSourcesBuild;
import io.quarkus.info.deployment.spi.InfoBuildTimeContributorBuildItem;
import io.quarkus.omnifaces.runtime.FacesInfoContributor;
import io.quarkus.omnifaces.runtime.OmniFacesFeature;
import io.quarkus.omnifaces.runtime.OmniFacesRecorder;
import io.quarkus.omnifaces.runtime.ParamBeanCreator;
import io.quarkus.omnifaces.runtime.scopes.OmniFacesQuarkusViewScope;
import io.quarkus.undertow.deployment.ServletInitParamBuildItem;

class OmnifacesProcessor {

    private static final Logger LOGGER = Logger.getLogger("OmniFaces");
    private static final String FEATURE = "omnifaces";

    static final DotName OMNIFACES_STARTUP = DotName.createSimple(Startup.class.getName());
    static final DotName OMNIFACES_EAGER = DotName.createSimple(Eager.class.getName());
    static final DotName OMNIFACES_PARAM = DotName.createSimple(Param.class.getName());
    static final DotName INJECTION_POINT = DotName.createSimple(InjectionPoint.class.getName());

    private static final Class<?>[] BEAN_CLASSES = {
            EagerBeansRepository.class,
            ValidatorManager.class,
            ViewScopeManager.class,
            ConverterManager.class,
            SocketChannelManager.class,
            SocketPushContextProducer.class,
            SocketChannelManager.ViewScope.class
    };

    private static final String[] BEAN_DEFINING_ANNOTATION_CLASSES = {
            ContextParam.class.getName(),
            Cookie.class.getName(),
            Eager.class.getName(),
            GraphicImageBean.class.getName(),
            Param.class.getName(),
            PostScriptParam.class.getName(),
            Push.class.getName(),
            Startup.class.getName()
    };

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep(onlyIf = NativeOrNativeSourcesBuild.class)
    NativeImageFeatureBuildItem nativeImageFeature() {
        return new NativeImageFeatureBuildItem(OmniFacesFeature.class);
    }

    @BuildStep
    void buildCdiBeans(BuildProducer<AdditionalBeanBuildItem> additionalBean,
            BuildProducer<BeanDefiningAnnotationBuildItem> beanDefiningAnnotation) {
        for (Class<?> clazz : BEAN_CLASSES) {
            additionalBean.produce(AdditionalBeanBuildItem.unremovableOf(clazz));
        }

        for (String clazz : BEAN_DEFINING_ANNOTATION_CLASSES) {
            beanDefiningAnnotation.produce(new BeanDefiningAnnotationBuildItem(DotName.createSimple(clazz)));
        }
    }

    @BuildStep
    ContextConfiguratorBuildItem registerViewScopeContext(ContextRegistrationPhaseBuildItem phase) {
        return new ContextConfiguratorBuildItem(
                phase.getContext().configure(ViewScoped.class).normal()
                        .contextClass(OmniFacesQuarkusViewScope.class));
    }

    @BuildStep
    CustomScopeBuildItem viewScoped() {
        return new CustomScopeBuildItem(DotName.createSimple(ViewScoped.class.getName()));
    }

    @BuildStep
    void produceApplicationArchiveMarker(
            BuildProducer<AdditionalApplicationArchiveMarkerBuildItem> additionalArchiveMarkers) {
        additionalArchiveMarkers.produce(new AdditionalApplicationArchiveMarkerBuildItem("org/omnifaces/component"));
    }

    @BuildStep
    void produceNativeResources(BuildProducer<NativeImageResourceBuildItem> nativeImageResourceProducer) {
        nativeImageResourceProducer
                .produce(new NativeImageResourceBuildItem("META-INF/maven/org.omnifaces/omnifaces/pom.properties"));
    }

    @BuildStep
    void produceKnownCompatible(BuildProducer<KnownCompatibleBeanArchiveBuildItem> knownCompatibleProducer) {
        // GitHub #62: bean discovery mode in beans.xml
        knownCompatibleProducer.produce(new KnownCompatibleBeanArchiveBuildItem("org.omnifaces", "omnifaces"));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void buildAnnotationProviderIntegration(OmniFacesRecorder recorder, CombinedIndexBuildItem combinedIndex) {
        for (String clazz : BEAN_DEFINING_ANNOTATION_CLASSES) {
            combinedIndex.getIndex()
                    .getAnnotations(DotName.createSimple(clazz))
                    .forEach(annotation -> {
                        if (annotation.target().kind() == AnnotationTarget.Kind.CLASS) {
                            recorder.registerAnnotatedClass(annotation.name().toString(),
                                    annotation.target().asClass().name().toString());
                        }
                    });
        }
    }

    @BuildStep
    void registerForReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            CombinedIndexBuildItem combinedIndex) {

        final List<String> classNames = new ArrayList<>();
        // All EL functions
        classNames.addAll(collectClassesInPackage(combinedIndex, "org.omnifaces.el.functions"));
        // All utilities
        classNames.addAll(collectClassesInPackage(combinedIndex, "org.omnifaces.util"));
        // socket
        classNames.add(org.omnifaces.cdi.push.SocketEndpoint.class.getName());

        reflectiveClass.produce(
                ReflectiveClassBuildItem.builder(classNames.toArray(new String[0])).methods(true).serialization(true).build());
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void registerWebManifests(OmniFacesRecorder recorder, BuildProducer<UnremovableBeanBuildItem> unremovableBeans) {
        // make WebManifest beans un-removable, users still have to make them beans
        // https://github.com/quarkiverse/quarkus-omnifaces/issues/72
        unremovableBeans.produce(UnremovableBeanBuildItem.beanTypes(WebAppManifest.class));
    }

    @BuildStep
    void substrateResourceBuildItems(BuildProducer<NativeImageResourceBuildItem> nativeImageResourceProducer,
            BuildProducer<NativeImageResourceBundleBuildItem> resourceBundleBuildItem) {
        nativeImageResourceProducer.produce(new NativeImageResourceBuildItem(
                "META-INF/omnifaces-functions.taglib.xml",
                "META-INF/omnifaces-ui.taglib.xml",
                "META-INF/web-fragment.xml",
                "META-INF/faces-config.xml",
                "META-INF/web.xml",
                "org/omnifaces/messages.properties",
                "META-INF/rsc/myfaces-dev-error-include.xml",
                "META-INF/services/jakarta.servlet.ServletContainerInitializer",
                "META-INF/maven/org.omnifaces/omnifaces/pom.properties"));

        resourceBundleBuildItem.produce(new NativeImageResourceBundleBuildItem("org.omnifaces.messages"));
    }

    /**
     * Builds development-specific initialization parameters.
     * This method is only executed in development mode.
     *
     * @param initParam The producer for servlet initialization parameters
     */
    @BuildStep(onlyIf = IsDevelopment.class)
    void buildDevelopmentInitParams(BuildProducer<ServletInitParamBuildItem> initParam) {
        // Disables combined resource handler in dev mode
        initParam.produce(new ServletInitParamBuildItem(CombinedResourceHandler.PARAM_NAME_DISABLED, "true"));
    }

    /**
     * Replaces {@link org.omnifaces.cdi.Eager} and {@link org.omnifaces.cdi.Startup} with the Quarkus equivalent
     * annotations for {@link jakarta.enterprise.context.ApplicationScoped} and {@link io.quarkus.runtime.Startup}.
     */
    @BuildStep
    AnnotationsTransformerBuildItem transformBeanScope() {

        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {
            @Override
            public boolean appliesTo(AnnotationTarget.Kind kind) {
                return kind == org.jboss.jandex.AnnotationTarget.Kind.CLASS;
            }

            @Override
            public void transform(AnnotationsTransformer.TransformationContext ctx) {

                if (ctx.isClass()) {
                    final ClassInfo clazz = ctx.getTarget().asClass();
                    final Map<DotName, List<AnnotationInstance>> annotations = clazz.annotationsMap();
                    if (annotations.containsKey(OMNIFACES_STARTUP)) {
                        LOGGER.debugf("OmniFaces found @%s annotations on a class %s - adding @ApplicationScoped",
                                OMNIFACES_STARTUP, ctx.getTarget());
                        ctx.transform().add(ApplicationScoped.class).done();
                    }
                    if (annotations.containsKey(OMNIFACES_EAGER) || annotations.containsKey(OMNIFACES_STARTUP)) {
                        LOGGER.debugf("OmniFaces found @Eager annotations on a class %s - adding @io.quarkus.runtime.Startup",
                                ctx.getTarget());
                        ctx.transform().add(io.quarkus.runtime.Startup.class).done();
                    }
                }
            }
        });
    }

    /**
     * Inspects injection points for @Param annotations and creates synthetic beans for each required type.
     * <p>
     * This method performs the following steps:
     * 1. Iterates through all injection points to find those annotated with @Param.
     * 2. Collects the required types for these injection points.
     * 3. For each required type, creates and registers a synthetic bean.
     *
     * @param beanDiscoveryFinishedBuildItem Contains information about discovered beans and injection points.
     * @param syntheticBeanBuildItemBuildProducer Producer for creating synthetic beans.
     */
    @BuildStep
    public void inspectInjectionPoints(BeanDiscoveryFinishedBuildItem beanDiscoveryFinishedBuildItem,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer) {
        // go through all injection points, look for those with @Param, store required types
        Set<Type> requiredParamTypes = new HashSet<>();
        for (InjectionPointInfo ipInfo : beanDiscoveryFinishedBuildItem.getInjectionPoints()) {
            if (ipInfo.getRequiredQualifier(OMNIFACES_PARAM) != null) {
                requiredParamTypes.add(ipInfo.getRequiredType());
            }
        }

        // for each required type, register a synthetic bean
        for (Type requiredType : requiredParamTypes) {
            SyntheticBeanBuildItem synthBean = SyntheticBeanBuildItem.configure(DotName.createSimple(Object.class))
                    .scope(Dependent.class)
                    .addType(requiredType)
                    .addQualifier(Param.class)
                    .unremovable()
                    .addInjectionPoint(ClassType.create(INJECTION_POINT))
                    .creator(ParamBeanCreator.class)
                    .done();
            syntheticBeanBuildItemBuildProducer.produce(synthBean);
        }
    }

    @BuildStep
    InfoBuildTimeContributorBuildItem produceInformation() {
        return new InfoBuildTimeContributorBuildItem(new FacesInfoContributor());
    }

    public List<String> collectClassesInPackage(CombinedIndexBuildItem combinedIndex, String packageName) {
        final List<String> classes = new ArrayList<>();
        final List<DotName> packages = new ArrayList<>(combinedIndex.getIndex().getSubpackages(packageName));
        packages.add(DotName.createSimple(packageName));
        for (DotName aPackage : packages) {
            final List<String> packageClasses = combinedIndex.getIndex()
                    .getClassesInPackage(aPackage)
                    .stream()
                    .map(ClassInfo::toString)
                    .toList();
            classes.addAll(packageClasses);
        }
        return classes;
    }
}