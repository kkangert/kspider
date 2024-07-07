package top.kangert.kspider.executor.function.extension;

import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExtension;
import top.kangert.kspider.util.ExtractUtils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElementFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Element.class;
    }

    @Comment("根据 xpath 提取内容")
    @Example("${elementVar.xpath('//title/text()')}")
    public static String xpath(Element element, String xpath) {
        return ExtractUtils.getValueByXPath(element, xpath);
    }


    @Comment("根据 xpath 提取内容")
    @Example("${elementVar.xpaths('//h2/text()')}")
    public static List<String> xpaths(Element element, String xpath) {
        return ExtractUtils.getValuesByXPath(element, xpath);
    }

    @Comment("根据正则表达式提取内容")
    @Example("${elementVar.regx('<title>(.*?)</title>')}")
    public static String regx(Element element, String regx) {
        return ExtractUtils.getFirstMatcher(element.html(), regx, true);
    }

    @Comment("根据正则表达式提取内容")
    @Example("${elementVar.regx('<title>(.*?)</title>',1)}")
    public static String regx(Element element, String regx, int groupIndex) {
        return ExtractUtils.getFirstMatcher(element.html(), regx, groupIndex);
    }

    @Comment("根据正则表达式提取内容")
    @Example("${elementVar.regx('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<String> regx(Element element, String regx, List<Integer> groups) {
        return ExtractUtils.getFirstMatcher(element.html(), regx, groups);
    }

    @Comment("根据正则表达式提取内容")
    @Example("${elementVar.regxs('<h2>(.*?)</h2>')}")
    public static List<String> regxs(Element element, String regx) {
        return ExtractUtils.getMatchers(element.html(), regx, true);
    }

    @Comment("根据正则表达式提取内容")
    @Example("${elementVar.regxs('<h2>(.*?)</h2>',1)}")
    public static List<String> regxs(Element element, String regx, int groupIndex) {
        return ExtractUtils.getMatchers(element.html(), regx, groupIndex);
    }

    @Comment("根据正则表达式提取内容")
    @Example("${elementVar.regxs('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<List<String>> regxs(Element element, String regx, List<Integer> groups) {
        return ExtractUtils.getMatchers(element.html(), regx, groups);
    }

    @Comment("根据 css 选择器提取内容")
    @Example("${elementVar.selector('div > a')}")
    public static Element selector(Element element, String cssQuery) {
        return element.selectFirst(cssQuery);
    }

    @Comment("根据 css 选择器提取内容")
    @Example("${elementVar.selectors('div > a')}")
    public static Elements selectors(Element element, String cssQuery) {
        return element.select(cssQuery);
    }

    @Comment("获取同级节点")
    @Example("${elementVar.subling()}")
    public static Elements subling(Element element) {
        return element.siblingElements();
    }

    @Comment("获取上级节点")
    @Example("${elementVar.parent()}")
    public static Element parent(Element element) {
        return element.parent();
    }

    @Comment("获取上级节点")
    @Example("${elementVar.parents()}")
    public static Elements parents(Element element) {
        return element.parents();
    }
}
