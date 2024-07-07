package top.kangert.kspider.script;

import lombok.extern.slf4j.Slf4j;
import top.kangert.kspider.expression.ExpressionTemplate;
import top.kangert.kspider.expression.ExpressionTemplateContext;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.graalvm.polyglot.Context;
import org.springframework.stereotype.Component;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Slf4j
public class ScriptManager {

    private static ScriptEngine scriptEngine;

    private static Set<String> functions = new HashSet<>();

    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    static {
        scriptEngine = createEngine();

        StringBuffer script = new StringBuffer();
        script.append("var ExpressionTemplate = Java.type('")
                .append(ExpressionTemplate.class.getName())
                .append("');")
                .append("var ExpressionTemplateContext = Java.type('")
                .append(ExpressionTemplateContext.class.getName())
                .append("');")
                .append("function _eval(expression) {")
                .append("return ExpressionTemplate.create(expression).render(ExpressionTemplateContext.get());")
                .append("}");
        try {
            scriptEngine.eval(script.toString());
        } catch (ScriptException e) {
            log.error("注册 _eval 函数失败", e);
        }
    }

    /**
     * 创建JS解析引擎
     * 
     * @return ScriptEngine <-- GraalJSScriptEngine
     */
    public static ScriptEngine createEngine() {
        Context.Builder contextBuilder = Context.newBuilder("js");

        // 允许访问所有（TODO:不安全）
        contextBuilder.allowAllAccess(true);
        GraalJSScriptEngine engine = GraalJSScriptEngine.create(null, contextBuilder);

        return engine;
    }

    /**
     * 清空所有函数
     */
    public static void clearFunctions() {
        functions.clear();
    }

    public static void lock() {
        lock.writeLock().lock();
    }

    public static void unlock() {
        lock.writeLock().unlock();
    }

    /**
     * JS函数注册
     * 
     * @param functionName 函数名称
     * @param parameters   函数参数
     * @param script       函数体
     */
    public static void registerFunction(String functionName, String parameters, String script) {
        try {
            // 校验JS函数是否合法
            validScript(functionName, parameters, script);

            // 加入JS函数列表
            functions.add(functionName);
            log.info("注册自定义函数 {} 成功", functionName);
        } catch (Exception e) {
            log.warn("注册自定义函数 {} 失败", functionName, e);
        }
    }

    /**
     * 拼接组装函数
     * 
     * @param functionName 函数名称
     * @param parameters   参数列表
     * @param script       函数体
     * @return 拼接好的函数
     */
    private static String concatScript(String functionName, String parameters, String script) {
        StringBuffer scriptBuffer = new StringBuffer();
        scriptBuffer.append("function ")
                .append(functionName)
                .append("(")
                .append(parameters == null ? "" : parameters)
                .append("){")
                .append(script)
                .append("}");
        return scriptBuffer.toString();
    }

    /**
     * 查询函数列表中是否存在同名函数
     * 
     * @param functionName 函数名称
     * @return 是否存在
     */
    public static boolean containsFunction(String functionName) {
        try {
            lock.readLock().lock();
            return functions.contains(functionName);
        } finally {
            lock.readLock().unlock();
        }
    }

    /***
     * JS脚本检验是否合法
     * 
     * @param functionName 函数名称
     * @param parameters   参数列表
     * @param script       方法体
     * @throws Exception 异常信息
     */
    public static void validScript(String functionName, String parameters, String script) throws Exception {
        scriptEngine.eval(concatScript(functionName, parameters, script));
    }

    /**
     * 自定义的函数执行器
     * 
     * @param context      EL表达式解析
     * @param functionName 函数名称
     * @param args         参数
     * @return 结果
     * @throws ScriptException
     * @throws NoSuchMethodException
     */
    public static Object eval(ExpressionTemplateContext context, String functionName, Object... args)
            throws ScriptException, NoSuchMethodException {
        if ("_eval".equals(functionName)) {
            if (args == null || args.length != 1) {
                throw new ScriptException("_eval 必须要有一个参数");
            } else {
                return ExpressionTemplate.create(args[0].toString()).render(context);
            }
        }
        if (scriptEngine == null) {
            throw new NoSuchMethodException(functionName);
        }
        try {
            lock.readLock().lock();
            return convertObject(((Invocable) scriptEngine).invokeFunction(functionName, args));
        } finally {
            lock.readLock().unlock();
        }
    }

    private static Object convertObject(Object object) {
        // if (object instanceof GraalJSBindings) {

        // ScriptObjectMirror mirror = (GraalJSBindings) object;
        // if (mirror.isArray()) {
        // int size = mirror.size();
        // Object[] array = new Object[size];
        // for (int i = 0; i < size; i++) {
        // array[i] = convertObject(mirror.getSlot(i));
        // }
        // return array;
        // } else {
        // String className = mirror.getClassName();
        // if ("Date".equalsIgnoreCase(className)) {
        // return new Date(mirror.to(Long.class));
        // }
        // // 其它类型待处理
        // }
        // }
        return object;
    }
}
