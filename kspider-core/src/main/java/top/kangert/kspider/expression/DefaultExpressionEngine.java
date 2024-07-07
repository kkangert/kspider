package top.kangert.kspider.expression;

import top.kangert.kspider.executor.FunctionExecutor;
import top.kangert.kspider.executor.FunctionExtension;
import top.kangert.kspider.expression.interpreter.AbstractReflection;
import top.kangert.kspider.io.SpiderResponse;
import top.kangert.kspider.support.ExpressionEngine;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统默认的表达式解析引擎
 */
@Component
public class DefaultExpressionEngine implements ExpressionEngine {

    @Autowired
    @SuppressWarnings("all")
    private List<FunctionExecutor> functionExecutors;

    @Autowired
    @SuppressWarnings("all")
    private List<FunctionExtension> functionExtensions;

    /**
     * 扩展类型
     */
    private List<Class<?>> extensionClasses = Arrays.asList(
            SpiderResponse.class,
            Element.class,
            Elements.class,
            String.class,
            Object.class,
            Date.class,
            Integer.class,
            List.class,
            Object[].class,
            Map.class,
            SqlRowSet.class);

    @PostConstruct
    private void init() {
        for (FunctionExtension extension : functionExtensions) {
            AbstractReflection.getInstance().registerExtensionClass(extension.support(), extension.getClass());
        }
    }

    public Map<String, ExpressionObject> getExpressionObjectMap() {

        Map<String, ExpressionObject> objectMap = new HashMap<>();

        functionExecutors.forEach(functionExecutor -> {
            ExpressionObject object = new ExpressionObject();
            object.setClassName(functionExecutor.getFunctionPrefix());
            object.setMethods(getMethod(functionExecutor.getClass(), true));
            objectMap.put(object.getClassName(), object);
        });

        extensionClasses.forEach(clazz -> {
            ExpressionObject object = new ExpressionObject();
            object.setClassName(clazz.getSimpleName());
            getMethod(clazz, false).forEach(method -> {
                if (method.getName().startsWith("get") && method.getParameters().size() == 0
                        && method.getName().length() > 3) {
                    String attributeName = method.getName().substring(3);
                    attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
                    object.addAttribute(
                            new ExpressionObject.ExpressionAttribute(method.getReturnType(), attributeName));
                } else {
                    object.addMethod(method);
                }
            });
            objectMap.put(object.getClassName(), object);
        });

        functionExtensions.forEach(extensions -> {
            ExpressionObject object = objectMap.get(extensions.support().getSimpleName());
            if (object != null) {
                getMethod(extensions.getClass(), true).forEach(method -> {
                    if (method.getParameters().size() > 0) {
                        method.getParameters().remove(0);
                        object.addMethod(method);
                    }
                });
            }
        });

        return objectMap;
    }

    private List<ExpressionObject.ExpressionMethod> getMethod(Class<?> clazz, boolean publicAndStatic) {
        List<ExpressionObject.ExpressionMethod> methods = new ArrayList<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            Method declaredMethod = declaredMethods[i];
            if (Modifier.isPublic(declaredMethod.getModifiers())) {
                boolean isStatic = Modifier.isStatic(declaredMethod.getModifiers());
                if ((!publicAndStatic) || isStatic) {
                    methods.add(new ExpressionObject.ExpressionMethod(declaredMethod));
                }
            }
        }
        return methods;
    }

    @Override
    public Object execute(String expression, Map<String, Object> variables) {
        if (StrUtil.isBlank(expression)) {
            return expression;
        }
        ExpressionTemplateContext context = new ExpressionTemplateContext(variables);
        for (FunctionExecutor executor : functionExecutors) {
            context.set(executor.getFunctionPrefix(), executor);
        }
        ExpressionGlobalVariables.getVariables().entrySet().forEach(entry -> {
            context.set(entry.getKey(), ExpressionTemplate.create(entry.getValue()).render(context));
        });
        try {
            ExpressionTemplateContext.set(context);
            return ExpressionTemplate.create(expression).render(context);
        } finally {
            ExpressionTemplateContext.remove();
        }
    }

}
