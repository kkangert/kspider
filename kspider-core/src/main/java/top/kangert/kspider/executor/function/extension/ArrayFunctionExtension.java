package top.kangert.kspider.executor.function.extension;

import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExtension;

import java.util.Arrays;
import java.util.List;

/**
 * 数组的方法扩展
 */
@Component
public class ArrayFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Object[].class;
    }

    @Comment("获取数组的长度")
    @Example("${arrayVar.size()}")
    public static int size(Object[] objs) {
        return objs.length;
    }

    @Comment("将数组用 separator 拼接起来")
    @Example("${arrayVar.join('-')}")
    public static String join(Object[] objs, String separator) {
        return StrUtil.join(separator, objs);
    }

    @Comment("将数组拼接起来")
    @Example("${arrayVar.join()}")
    public static String join(Object[] objs) {
        return StrUtil.join(",", objs);
    }

    @Comment("将数组转为 List")
    @Example("${arrayVar.toList()}")
    public static List<?> toList(Object[] objs) {
        return Arrays.asList(objs);
    }

}
