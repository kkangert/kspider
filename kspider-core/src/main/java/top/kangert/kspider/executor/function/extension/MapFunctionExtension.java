package top.kangert.kspider.executor.function.extension;

import org.springframework.stereotype.Component;

import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Java的Map扩展
 */
@Component
public class MapFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Map.class;
    }

    @Comment("将 map 转换为 List")
    @Example("${mapVar.toList('=')}")
    public static List<String> toList(Map<?, ?> map, String separator) {
        return map.entrySet().stream().map(entry -> entry.getKey() + separator + entry.getValue()).collect(Collectors.toList());
    }
}
