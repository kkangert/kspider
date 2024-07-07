package top.kangert.kspider.executor.function.extension;

import org.springframework.stereotype.Component;

import cn.hutool.core.date.DateUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExtension;

import java.util.Date;

/**
 * 日期格式化函数扩展
 */
@Component
public class DateFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Date.class;
    }

    @Comment("格式化日期")
    @Example("${dateVar.format()}")
    public static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    @Comment("格式化日期")
    @Example("${dateVar.format('yyyy-MM-dd HH:mm:ss')}")
    public static String format(Date date, String pattern) {
        return DateUtil.format(date, pattern);
    }
}
