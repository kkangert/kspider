package top.kangert.kspider.executor.function.extension;

import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExtension;
import top.kangert.kspider.executor.function.DateFunctionExecutor;
import top.kangert.kspider.util.ExtractUtils;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.json.JSONUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Java的String扩展
 */
@Component
public class StringFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return String.class;
    }

    @Comment("根据正则表达式提取 String 中的内容")
    @Example("${strVar.regx('<title>(.*?)</title>')}")
    public static String regx(String source, String pattern) {
        return ExtractUtils.getFirstMatcher(source, pattern, true);
    }

    @Comment("根据正则表达式提取 String 中的内容")
    @Example("${strVar.regx('<title>(.*?)</title>',1)}")
    public static String regx(String source, String pattern, int groupIndex) {
        return ExtractUtils.getFirstMatcher(source, pattern, groupIndex);
    }

    @Comment("根据正则表达式提取 String 中的内容")
    @Example("${strVar.regx('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<String> regx(String source, String pattern, List<Integer> groups) {
        return ExtractUtils.getFirstMatcher(source, pattern, groups);
    }

    @Comment("根据正则表达式提取 String 中的内容")
    @Example("${strVar.regxs('<h2>(.*?)</h2>')}")
    public static List<String> regxs(String source, String pattern) {
        return ExtractUtils.getMatchers(source, pattern, true);
    }

    @Comment("根据正则表达式提取 String 中的内容")
    @Example("${strVar.regxs('<h2>(.*?)</h2>',1)}")
    public static List<String> regxs(String source, String pattern, int groupIndex) {
        return ExtractUtils.getMatchers(source, pattern, groupIndex);
    }

    @Comment("根据正则表达式提取 String 中的内容")
    @Example("${strVar.regxs('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<List<String>> regxs(String source, String pattern, List<Integer> groups) {
        return ExtractUtils.getMatchers(source, pattern, groups);
    }

    @Comment("根据 xpath 在 String 变量中查找")
    @Example("${strVar.xpath('//title/text()')}")
    public static String xpath(String source, String xpath) {
        return ExtractUtils.getValueByXPath(element(source), xpath);
    }

    @Comment("根据 xpath 在 String 变量中查找")
    @Example("${strVar.xpaths('//a/@href')}")
    public static List<String> xpaths(String source, String xpath) {
        return ExtractUtils.getValuesByXPath(element(source), xpath);
    }

    @Comment("将 String 变量转为 Element 对象")
    @Example("${strVar.element()}")
    public static Element element(String source) {
        return Parser.xmlParser().parseInput(source, "");
    }

    @Comment("根据 css 选择器提取")
    @Example("${strVar.selector('div > a')}")
    public static Element selector(String source, String cssQuery) {
        return element(source).selectFirst(cssQuery);
    }

    @Comment("根据 css 选择器提取")
    @Example("${strVar.selector('div > a')}")
    public static Elements selectors(String source, String cssQuery) {
        return element(source).select(cssQuery);
    }

    @Comment("将 string 转为 json 对象")
    @Example("${strVar.json()}")
    public static Object json(String source) {
        return JSONUtil.parse(source);
    }

    @Comment("根据 jsonpath 提取内容")
    @Example("${strVar.jsonpath('$.code')}")
    public static Object jsonpath(String source, String jsonPath) {
        return ExtractUtils.getValueByJsonPath(json(source), jsonPath);
    }

    @Comment("将字符串转为 int 类型")
    @Example("${strVar.toInt(0)}")
    public static Integer toInt(String source, int defaultValue) {
        return Convert.toInt(source, defaultValue);
    }

    @Comment("将字符串转为 int 类型")
    @Example("${strVar.toInt()}")
    public static Integer toInt(String source) {
        return Convert.toInt(source, 0);
    }

    @Comment("将字符串转为 double 类型")
    @Example("${strVar.toDouble()}")
    public static Double toDouble(String source) {
        return Convert.toDouble(source, 0.0);
    }

    @Comment("将字符串转为 long 类型")
    @Example("${strVar.toLong()}")
    public static Long toLong(String source) {
        return Convert.toLong(source, 0L);
    }

    @Comment("将字符串转为 date 类型")
    @Example("${strVar.toDate('yyyy-MM-dd HH:mm:ss')}")
    public static Date toDate(String source, String pattern) throws ParseException {
        return DateFunctionExecutor.parse(source, pattern);
    }

    @Comment("反转义字符串")
    @Example("${strVar.unescape()}")
    public static String unescape(String source) {
        return EscapeUtil.safeUnescape(source);
    }
}
