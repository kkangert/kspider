package top.kangert.kspider.executor.function;

import org.springframework.stereotype.Component;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;

import java.text.ParseException;
import java.util.Date;

/**
 * 时间获取/格式化（默认格式 yyyy-MM-dd HH:mm:ss）
 */
@Component
@Comment("日期常用方法")
public class DateFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "date";
    }

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Comment("格式化日期")
    @Example("${date.format(date.now())}")
    public static String format(Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    @Comment("格式化日期")
    @Example("${date.format(1569059534000l)}")
    public static String format(Long millis) {
        return format(millis, DEFAULT_PATTERN);
    }

    @Comment("格式化日期")
    @Example("${date.format(date.now(),'yyyy-MM-dd')}")
    public static String format(Date date, String pattern) {
        return date != null ? DateUtil.format(date, pattern) : null;
    }

    @Comment("格式化日期")
    @Example("${date.format(1569059534000l,'yyyy-MM-dd')}")
    public static String format(Long millis, String pattern) {
        return millis != null ? DateUtil.format(new Date(millis), pattern) : null;
    }

    @Comment("字符串转为日期类型")
    @Example("${date.parse('2019-01-01 00:00:00')}")
    public static Date parse(String date) throws ParseException {
        return date != null ? DateUtil.parse(date, DEFAULT_PATTERN).toJdkDate() : null;
    }

    @Comment("字符串转为日期类型")
    @Example("${date.parse('2019-01-01','yyyy-MM-dd')}")
    public static Date parse(String date, String pattern) throws ParseException {
        return date != null ? DateUtil.parse(date, pattern).toJdkDate() : null;
    }

    @Comment("数字为日期类型")
    @Example("${date.parse(1569059534000l)}")
    public static Date parse(Long millis) {
        return new Date(millis);
    }

    @Comment("获取当前时间")
    @Example("${date.now()}")
    public static Date now() {
        return new Date();
    }

    @Comment("获取指定日期 n 年后的日期")
    @Example("${date.addYears(date.now(), 2)}")
    public static Date addYears(Date date, int amount) {
        DateTime tempDate = DateUtil.date(date);
        tempDate = tempDate.offset(DateField.YEAR, amount);
        return tempDate.toJdkDate();
    }

    @Comment("获取指定日期 n 月后的日期")
    @Example("${date.addMonths(date.now(), 2)}")
    public static Date addMonths(Date date, int amount) {
        DateTime tempDate = DateUtil.date(date);
        tempDate = tempDate.offset(DateField.MONTH, amount);
        return tempDate.toJdkDate();
    }

    @Comment("获取指定日期 n 天后的日期")
    @Example("${date.addDays(date.now(), 2)}")
    public static Date addDays(Date date, int amount) {
        DateTime tempDate = DateUtil.date(date);
        tempDate = tempDate.offset(DateField.DAY_OF_YEAR, amount);
        return tempDate.toJdkDate();
    }

    @Comment("获取指定日期 n 小时后的日期")
    @Example("${date.addHours(date.now(), 2)}")
    public static Date addHours(Date date, int amount) {
        DateTime tempDate = DateUtil.date(date);
        tempDate = tempDate.offset(DateField.HOUR_OF_DAY, amount);
        return tempDate.toJdkDate();
    }

    @Comment("获取指定日期 n 分钟后的日期")
    @Example("${date.addMinutes(date.now(), 2)}")
    public static Date addMinutes(Date date, int amount) {
        DateTime tempDate = DateUtil.date(date);
        tempDate = tempDate.offset(DateField.MINUTE, amount);
        return tempDate.toJdkDate();
    }

    @Comment("获取指定日期 n 秒后的日期")
    @Example("${date.addSeconds(date.now(), 2)}")
    public static Date addSeconds(Date date, int amount) {
        DateTime tempDate = DateUtil.date(date);
        tempDate = tempDate.offset(DateField.SECOND, amount);
        return tempDate.toJdkDate();
    }
}
