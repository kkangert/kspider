package top.kangert.kspider.executor.function;

import org.springframework.stereotype.Component;

import cn.hutool.json.JSONUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;

/**
 * Json 和 String 互相转换
 */
@Component
@Comment("json 常用方法")
public class JsonFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "json";
    }

    @Comment("将字符串转为 json 对象")
    @Example("${json.parse('{code : 1}')}")
    public static Object parse(String jsonString) {
        return jsonString != null ? JSONUtil.parse(jsonString) : null;
    }

    @Comment("将对象转为 json 字符串")
    @Example("${json.stringify(objVar)}")
    public static String stringify(Object object) {
        return object != null ? JSONUtil.toJsonStr(object) : null;
    }
}
