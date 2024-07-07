package top.kangert.kspider.executor.function.extension;

import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExtension;
import top.kangert.kspider.util.ExtractUtils;

import org.springframework.stereotype.Component;

import cn.hutool.json.JSONUtil;

import java.util.Objects;

/**
 * Java的Object的扩展
 */
@Component
public class ObjectFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Object.class;
    }

    @Comment("将对象转为 string 类型")
    @Example("${objVar.string()}")
    public static String string(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        return Objects.toString(obj);
    }

    @Comment("根据 jsonpath 提取内容")
    @Example("${objVar.jsonpath('$.code')}")
    public static Object jsonpath(Object obj, String path) {
        if (obj instanceof String) {
            return ExtractUtils.getValueByJsonPath(JSONUtil.parse((String) obj), path);
        }
        return ExtractUtils.getValueByJsonPath(obj, path);
    }

    @Comment("睡眠等待一段时间")
    @Example("${objVar.sleep(1000)}")
    public static Object sleep(Object obj, int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
        return obj;
    }
}
