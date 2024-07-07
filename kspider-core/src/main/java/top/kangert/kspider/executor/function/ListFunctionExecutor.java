package top.kangert.kspider.executor.function;

import org.springframework.stereotype.Component;

import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * List 工具类
 * 添加了类似 python 的 split 方法
 */
@Component
@Comment("list 常用方法")
public class ListFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "list";
    }

    @Comment("获取 list 的长度")
    @Example("${list.length(listVar)}")
    public static int length(List<?> list) {
        return list != null ? list.size() : 0;
    }

    /**
     * @param list 原 List
     * @param len  按多长进行分割
     * @return List<List < ?>> 分割后的数组
     */
    @Comment("分割 List")
    @Example("${list.split(listVar,10)}")
    public static List<List<?>> split(List<?> list, int len) {
        List<List<?>> result = new ArrayList<>();
        if (list == null || list.size() == 0 || len < 1) {
            return result;
        }
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<?> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

    @Comment("截取 List")
    @Example("${list.sublist(listVar,fromIndex,toIndex)}")
    public static List<?> sublist(List<?> list, int fromIndex, int toIndex) {
        return list != null ? list.subList(fromIndex, toIndex) : new ArrayList<>();
    }

    @Comment("过滤字符串 list 元素")
    @Example("${listVar.filterStr(pattern)}")
    public static List<String> filterStr(List<String> list, String pattern) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<String> result = new ArrayList<>(list.size());
        for (String item : list) {
            if (Pattern.matches(pattern, item)) {
                result.add(item);
            }
        }
        return result;
    }

}
