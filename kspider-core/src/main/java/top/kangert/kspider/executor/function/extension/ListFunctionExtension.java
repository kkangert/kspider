package top.kangert.kspider.executor.function.extension;

import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExtension;

import java.util.Collections;
import java.util.List;

/**
 * Java的List扩展
 */
@Component
public class ListFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return List.class;
    }

    @Comment("获取 list 的长度")
    @Example("${listVar.length()}")
    public static int length(List<?> list) {
        return list.size();
    }

    @Comment("将 list 拼接起来")
    @Example("${listVar.join()}")
    public static String join(List<?> list) {
        return StrUtil.join(",", list.toArray());
    }

    @Comment("将 list 用 separator 拼接起来")
    @Example("${listVar.join('-')}")
    public static String join(List<?> list, String separator) {
        if (list.size() == 1) {
            return list.get(0).toString();
        } else {
            return StrUtil.join(separator, list.toArray());
        }
    }

    @Comment("将 list<String> 排序")
    @Example("${listVar.sort()}")
    public static List<String> sort(List<String> list) {
        Collections.sort(list);
        return list;
    }

    @Comment("将 list 打乱顺序")
    @Example("${listVar.shuffle()}")
    public static List<?> shuffle(List<?> list) {
        Collections.shuffle(list);
        return list;
    }

}
