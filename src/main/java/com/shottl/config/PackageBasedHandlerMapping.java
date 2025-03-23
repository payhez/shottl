package com.shottl.config;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class PackageBasedHandlerMapping extends RequestMappingHandlerMapping {

    private static final String BASE_PACKAGE = "com.shottl.controller";

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        Class<?> handlerType = method.getDeclaringClass();
        String prefix = extractPrefixFromPackage(handlerType.getPackage().getName(), BASE_PACKAGE);
        if (!prefix.isEmpty()) {
            RequestMappingInfo prefixMapping = RequestMappingInfo.paths(prefix).build();
            mapping = prefixMapping.combine(mapping);
        }
        super.registerHandlerMethod(handler, method, mapping);
    }

    private String extractPrefixFromPackage(String fullPackage, String basePackage) {
        if (fullPackage.startsWith(basePackage)) {
            String subPackage = fullPackage.substring(basePackage.length());
            return subPackage.replace('.', '/');
        }
        return "";
    }
}
