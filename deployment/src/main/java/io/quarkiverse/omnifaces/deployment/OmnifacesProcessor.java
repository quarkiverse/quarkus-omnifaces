package io.quarkiverse.omnifaces.deployment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
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
import org.omnifaces.cdi.validator.ValidatorManager;
import org.omnifaces.cdi.viewscope.ViewScopeManager;
import org.omnifaces.resourcehandler.CombinedResourceHandler;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.deployment.ContextRegistrationPhaseBuildItem;
import io.quarkus.arc.deployment.ContextRegistrationPhaseBuildItem.ContextConfiguratorBuildItem;
import io.quarkus.arc.deployment.CustomScopeAnnotationsBuildItem;
import io.quarkus.arc.deployment.CustomScopeBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
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
import io.quarkus.omnifaces.runtime.OmniFacesFeature;
import io.quarkus.omnifaces.runtime.OmniFacesRecorder;
import io.quarkus.omnifaces.runtime.scopes.OmniFacesQuarkusViewScope;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ConfigUtils;
import io.quarkus.undertow.deployment.ServletInitParamBuildItem;

class OmnifacesProcessor {

    private static final Logger LOGGER = Logger.getLogger("OmnifacesProcessor");

    private static final String FEATURE = "omnifaces";
    static final DotName OMNIFACES_STARTUP = DotName.createSimple(Startup.class.getName());
    static final DotName OMNIFACES_EAGER = DotName.createSimple(Eager.class.getName());

    private static final Class[] BEAN_CLASSES = {
            EagerBeansRepository.class,
            ValidatorManager.class,
            ViewScopeManager.class,
            ConverterManager.class
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

    @BuildStep
    NativeImageFeatureBuildItem nativeImageFeature() {
        return new NativeImageFeatureBuildItem(OmniFacesFeature.class);
    }

    @BuildStep
    void buildCdiBeans(BuildProducer<AdditionalBeanBuildItem> additionalBean,
            BuildProducer<BeanDefiningAnnotationBuildItem> beanDefiningAnnotation) throws IOException {
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
    void produceNativeResources(BuildProducer<NativeImageResourceBuildItem> nativeImageResourceProducer,
            BuildProducer<NativeImageResourceBundleBuildItem> resourceBundleBuildItem) {
        nativeImageResourceProducer
                .produce(new NativeImageResourceBuildItem("META-INF/maven/org.omnifaces/omnifaces/pom.properties"));
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void buildAnnotationProviderIntegration(OmniFacesRecorder recorder, CombinedIndexBuildItem combinedIndex)
            throws IOException {
        for (String clazz : BEAN_DEFINING_ANNOTATION_CLASSES) {
            combinedIndex.getIndex()
                    .getAnnotations(DotName.createSimple(clazz))
                    .stream()
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

        //most of the classes registered for reflection below are used in OmniFaces functions (omnifaces-functions.taglib.xml)
        //myfaces (org.apache.myfaces.view.facelets.compiler.TagLibraryConfig.create) uses reflection to register facelets functions
        reflectiveClass.produce(new ReflectiveClassBuildItem(false, false, "java.util.Set",
                "java.util.List",
                "java.lang.Iterable",
                "java.util.Collection",
                "java.lang.Throwable",
                "java.util.Date",
                "java.util.Calendar",
                "java.time.LocalDate",
                "java.time.LocalDateTime",
                "java.time.OffsetDateTime",
                "java.time.ZonedDateTime",
                "java.lang.Integer",
                "java.lang.Long",
                "java.lang.Byte",
                "java.lang.Double",
                "java.lang.String",
                "java.lang.Number"));

        final List<String> classNames = new ArrayList<>();
        // All EL functions
        classNames.addAll(collectClassesInPackage(combinedIndex, org.omnifaces.el.functions.Strings.class.getPackageName()));
        // All utilities
        classNames.addAll(collectClassesInPackage(combinedIndex, org.omnifaces.util.Beans.class.getPackageName()));

        // TODO: REMOVE PrimeFaces Extensions Utilities
        classNames.addAll(Arrays.asList("org.primefaces.extensions.util.CommonMarkWrapper",
                "org.primefaces.extensions.util.ExtLangUtils",
                "org.primefaces.extensions.util.MessageFactory",
                "org.primefaces.extensions.util.PhoneNumberUtilWrapper",
                "org.primefaces.extensions.util.URLEncoderWrapper"));

        // TODO: Register CDI produced servlet objects (being fixed in MyFaces 2.3-M8)
        classNames.add(io.undertow.servlet.spec.HttpServletRequestImpl.class.getName());
        classNames.add(io.undertow.servlet.spec.HttpServletResponseImpl.class.getName());
        classNames.add(io.undertow.servlet.spec.HttpSessionImpl.class.getName());

        reflectiveClass.produce(new ReflectiveClassBuildItem(true, false, classNames.toArray(new String[classNames.size()])));
    }

    @BuildStep
    void substrateResourceBuildItems(BuildProducer<NativeImageResourceBuildItem> nativeImageResourceProducer,
            BuildProducer<NativeImageResourceBundleBuildItem> resourceBundleBuildItem) {
        nativeImageResourceProducer.produce(new NativeImageResourceBuildItem(
                "META-INF/omnifaces-functions.taglib.xml",
                "META-INF/omnifaces-ui.taglib.xml",
                "META-INF/web-fragment.xml",
                "META-INF/faces-config.xml",
                "META-INF/beans.xml",
                "org/omnifaces/messages.properties",
                "META-INF/rsc/myfaces-dev-error-include.xml",
                "META-INF/services/javax.servlet.ServletContainerInitializer",
                "META-INF/maven/org.omnifaces/omnifaces/pom.properties",
                "META-INF/resources/omnifaces/fixviewstate.js",
                "META-INF/resources/omnifaces/omnifaces.js",
                "META-INF/resources/omnifaces/sw.js"));

        resourceBundleBuildItem.produce(new NativeImageResourceBundleBuildItem("org.omnifaces.messages"));
    }

    @BuildStep
    void buildRecommendedInitParams(BuildProducer<ServletInitParamBuildItem> initParam) throws IOException {
        //disables combined resource handler in dev mode
        if (ConfigUtils.getProfiles().contains(LaunchMode.DEVELOPMENT.getDefaultProfile())) {
            initParam.produce(new ServletInitParamBuildItem(CombinedResourceHandler.PARAM_NAME_DISABLED, "true"));
        }
    }

    /**
     * Replace {@link org.omnifaces.cdi.Eager} and {@link org.omnifaces.cdi.Startup with the Quarkus equivalent
     * annotations for ApplicationScoped and Startup.
     */
    @BuildStep
    AnnotationsTransformerBuildItem transformBeanScope(BeanArchiveIndexBuildItem index,
            CustomScopeAnnotationsBuildItem scopes) {
        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {
            @Override
            public boolean appliesTo(AnnotationTarget.Kind kind) {
                return kind == org.jboss.jandex.AnnotationTarget.Kind.CLASS;
            }

            @Override
            public void transform(AnnotationsTransformer.TransformationContext ctx) {
                if (ctx.isClass()) {
                    ClassInfo clazz = ctx.getTarget().asClass();
                    Map<DotName, List<AnnotationInstance>> annotations = clazz.annotationsMap();
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

    public List<String> collectClassesInPackage(CombinedIndexBuildItem combinedIndex, String packageName) {
        List<String> classes = combinedIndex.getIndex()
                .getClassesInPackage(packageName)
                .stream()
                .map(ClassInfo::toString)
                .collect(Collectors.toList());
        return classes;
    }

    public List<String> collectSubclasses(CombinedIndexBuildItem combinedIndex, String className) {
        List<String> classes = combinedIndex.getIndex()
                .getAllKnownSubclasses(DotName.createSimple(className))
                .stream()
                .map(ClassInfo::toString)
                .collect(Collectors.toList());
        classes.add(className);
        return classes;
    }

    public List<String> collectImplementors(CombinedIndexBuildItem combinedIndex, String className) {
        List<String> classes = combinedIndex.getIndex()
                .getAllKnownSubclasses(DotName.createSimple(className))
                .stream()
                .map(ClassInfo::toString)
                .collect(Collectors.toList());
        classes.add(className);
        return classes;
    }
}
