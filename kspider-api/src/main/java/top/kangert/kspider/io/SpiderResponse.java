package top.kangert.kspider.io;

import cn.hutool.json.JSONUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;

import java.io.InputStream;
import java.util.Map;

public interface SpiderResponse {

    @Comment("获取返回状态码")
    @Example("${resp.statusCode}")
    int getStatusCode();

    @Comment("获取网页标题")
    @Example("${resp.title}")
    String getTitle();

    @Comment("获取网页 html")
    @Example("${resp.html}")
    String getHtml();

    @Comment("获取 json")
    @Example("${resp.json}")
    default Object getJson() {
        return JSONUtil.parse(getHtml());
    }

    @Comment("获取 cookies")
    @Example("${resp.cookies}")
    Map<String, String> getCookies();

    @Comment("获取 headers")
    @Example("${resp.headers}")
    Map<String, String> getHeaders();

    @Comment("获取 byte[]")
    @Example("${resp.bytes}")
    byte[] getBytes();

    @Comment("获取 ContentType")
    @Example("${resp.contentType}")
    String getContentType();

    @Comment("获取当前 url")
    @Example("${resp.url}")
    String getUrl();

    @Example("${resp.setCharset('UTF-8')}")
    default void setCharset(String charset) {
    }

    @Example("${resp.stream}")
    default InputStream getStream() {
        return null;
    }
}
