package org.emaginalabs.beanproxy.core.controller.proxy;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.emaginalabs.beanproxy.core.beans.proxy.*;
import org.emaginalabs.beanproxy.core.utils.ClassPathScanner;
import org.emaginalabs.beanproxy.core.utils.ClassUtils;
import org.emaginalabs.beanproxy.core.web.bind.annotation.ArgumentsWrapperBean;
import org.emaginalabs.beanproxy.core.web.bind.annotation.MultipleArgumentsWrapperBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jose
 */
public class BeanProxyBinder implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(BeanProxyBinder.class);

    private final ConcurrentMap<String, Class<?>> wrapperBeans = new ConcurrentHashMap<String, Class<?>>();

    private final ConcurrentMap<Class<?>, Object> bindinInfoCache;

    private String resourcePattern;

    @Value("org.emaginalabs")
    private String basePackage;

    private ParameterNameDiscoverer parameterNameDiscoverer;

    @Autowired(required = false)
    protected ObjectMapper objectMapper;

    private TypeFactory typeFactory = TypeFactory.defaultInstance();


    public BeanProxyBinder() {
        Cache<Class<?>, Object> tmpCache = CacheBuilder.newBuilder().maximumSize(100).build();
        bindinInfoCache = tmpCache.asMap();
        parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        objectMapper = new ObjectMapper();
    }


    public void afterPropertiesSet() throws Exception {

        searchArgumentsWrapperBeans();

    }

    @SuppressWarnings("unchecked")
    private void searchArgumentsWrapperBeans() throws ClassNotFoundException, LinkageError {

        Set<MetadataReader> candidateClasses = findCandidatesClasses();

        Class<ArgumentsWrapper> clazz;
        MultipleArgumentsWrapperBean multipleAnn;
        ArgumentsWrapperBean simpleAnn;

        for (MetadataReader metadata : candidateClasses) {
            clazz = (Class<ArgumentsWrapper>) ClassUtils.forName(metadata.getClassMetadata().getClassName(), null);

            if (AbstractArgumentsWrapper.class.isAssignableFrom(clazz)) {

                multipleAnn = AnnotationUtils.findAnnotation(clazz, MultipleArgumentsWrapperBean.class);

                if (multipleAnn == null) {
                    simpleAnn = AnnotationUtils.findAnnotation(clazz, ArgumentsWrapperBean.class);
                    processArgumentWrapperBeanAnnotation(simpleAnn, clazz);
                } else {
                    processMultipleArgumentsWrapperBean(multipleAnn, clazz);
                }

            }

        }

        logArgumentWrapperBeansFound();

    }

    private Set<MetadataReader> findCandidatesClasses() {

        ClassPathScanner scanner = new ClassPathScanner();

        if (resourcePattern != null) {
            scanner.setResourcePattern(resourcePattern);
        }

        scanner.addIncludeFilter(new AnnotationTypeFilter(ArgumentsWrapperBean.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(MultipleArgumentsWrapperBean.class));

        return scanner.findCandidateComponents(basePackage);
    }

    private void processMultipleArgumentsWrapperBean(MultipleArgumentsWrapperBean annotation, Class<ArgumentsWrapper> clazz) {

        for (ArgumentsWrapperBean ann : annotation.value()) {
            processArgumentWrapperBeanAnnotation(ann, clazz);
        }

    }

    private void processArgumentWrapperBeanAnnotation(ArgumentsWrapperBean annotation, Class<ArgumentsWrapper> clazz) {
        wrapperBeans.put(buildCacheKey(annotation.className(), annotation.method()), clazz);
    }

    private String buildCacheKey(String className, String methodName) {
        return className + '.' + methodName;
    }

    private void logArgumentWrapperBeansFound() {
        if (LOG.isDebugEnabled()) {

            if (wrapperBeans.isEmpty()) {
                LOG.debug("No Method arguments wrapper beans found");
            } else {
                StringBuilder sbLog = new StringBuilder();
                sbLog.append("Method arguments wrapper beans found:");
                for (Entry<String, Class<?>> aEntry : wrapperBeans.entrySet()) {
                    sbLog.append("\n\t- ").append(aEntry.getValue().getName()).append(" [").append(aEntry.getKey()).append(']');
                }
                LOG.debug(sbLog.toString());
            }

        }
    }

    public ArgumentsWrapper bind(Method method, JsonNode data) throws JsonParseException, JsonMappingException, IOException {
        if (data.isArray()) {
            return bindArray(method, (ArrayNode) data);
        } else {
            return bindObject(method, data);
        }
    }

    public Object bindingInfo(Method method) throws InstantiationException, IllegalAccessException {
        try {
            Class<?> bindingClass = getBindingClass(method);
            return getInstance(bindingClass);
        } catch (IllegalStateException illegalStateEx) {
            return getInstance(method.getParameterTypes());
        }
    }

    private ArgumentsWrapper bindArray(Method method, ArrayNode arrayData) throws JsonParseException, JsonMappingException, IOException {
        Object[] arguments = convertArguments(arrayData, method.getGenericParameterTypes());
        return new NoArgumentsWrapper(arguments);
    }

    private ArgumentsWrapper bindObject(Method method, JsonNode data) throws JsonParseException, JsonMappingException, IOException {
        Class<?> bindingClass = getBindingClass(method);
        return bind(data, bindingClass);
    }

    @Deprecated
    private Object[] convertArguments(ArrayNode jsonData, Class<?>[] paramTypes) throws JsonParseException, JsonMappingException, IOException {

        Object[] args = new Object[paramTypes.length];
        int idx = 0;

        for (Class<?> paramType : paramTypes) {
            args[idx] = objectMapper.convertValue(jsonData.get(idx++), paramType);
        }

        return args;
    }

    private Object[] convertArguments(ArrayNode jsonData, Type[] paramTypes) throws JsonParseException, JsonMappingException, IOException {

        Object[] args = new Object[paramTypes.length];
        int idx = 0;

        for (Type paramType : paramTypes) {
            args[idx] = objectMapper.convertValue(jsonData.get(idx++), typeFactory.constructType(paramType));
        }

        return args;
    }


    private Class<?> getBindingClass(Method method) {

        Class<?> declaringClass = ClassUtils.getDeclaringClass(method);
        String key = buildCacheKey(declaringClass.getName(), method.getName());
        Class<?> bindingClass = null;

        if (wrapperBeans.containsKey(key)) {
            bindingClass = wrapperBeans.get(key);
        } else {
            bindingClass = resolveBindingClass(method);
            wrapperBeans.putIfAbsent(key, bindingClass);
        }

        return bindingClass;
    }

    private Class<?> resolveBindingClass(Method method) {

        Class<?>[] parameterTypes = method.getParameterTypes();

        switch (parameterTypes.length) {
            case 0:
                return EmptyArgumentsWrapper.class;
            case 1:
                return parameterTypes[0];
            default:
                return createBindingClass(method);
        }

    }

    private Class<?> createBindingClass(Method method) {

        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);

        if (paramNames == null) {
            throw new IllegalStateException("Can't create binding class for method " + method);
        } else {

            Class<?>[] paramTypes = method.getParameterTypes();

            BeanGenerator beanGen = new BeanGenerator();

            for (int idx = 0; idx < paramNames.length; idx++) {
                beanGen.addProperty(paramNames[idx], paramTypes[idx]);
            }

            beanGen.setSuperclass(GeneratedArgumentsWrapper.class);
            Class<?> generatedBeanClass = (Class<?>) beanGen.createClass();
            GeneratedArgumentsWrapper.registerGeneratedBean(generatedBeanClass, paramNames);

            return generatedBeanClass;

        }
    }

    private ArgumentsWrapper bind(JsonNode data, Class<?> bindingClass) throws JsonParseException, JsonMappingException, IOException {

        if (EmptyArgumentsWrapper.class.equals(bindingClass)) {
            return EmptyArgumentsWrapper.INSTANCE;
        } else {
            Object obj = objectMapper.convertValue(data, bindingClass);

            if (obj instanceof ArgumentsWrapper) {
                return (ArgumentsWrapper) obj;
            } else {
                return new SingleArgumentsWrapper(obj);
            }

        }

    }

    private Object getInstance(Class<?>[] parameterTypes) throws InstantiationException, IllegalAccessException {

        Object[] instance = new Object[parameterTypes.length];
        int idx = 0;

        for (Class<?> parameterType : parameterTypes) {
            instance[idx++] = getInstance(parameterType);
        }

        return instance;

    }

    private Object getInstance(Class<?> bindingClass) throws InstantiationException, IllegalAccessException {

        if (!bindinInfoCache.containsKey(bindingClass)) {
            Object instance = createInstance(bindingClass);
            if (instance != null) {
                bindinInfoCache.putIfAbsent(bindingClass, instance);
            }
        }

        return bindinInfoCache.get(bindingClass);
    }

    private Object createInstance(Class<?> bindingClass) throws IllegalAccessException, InstantiationException {

        return bindingClass.newInstance();
    }


    public void setResourcePattern(String resourcePattern) {
        this.resourcePattern = resourcePattern;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    Map<String, Class<?>> getWrapperBeans() {
        return Collections.unmodifiableMap(wrapperBeans);
    }

}
