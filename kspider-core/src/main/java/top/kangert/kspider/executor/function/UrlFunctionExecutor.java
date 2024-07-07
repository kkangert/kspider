package top.kangert.kspider.executor.function;

import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * url 按指定字符集进行编码/解码 默认字符集（UTF-8）
 */
@Component
public class UrlFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "url";
    }

    @Comment("获取 url 参数")
    @Example("${url.parameter('https://www.baidu.com/s?wd=spider-flow','wd')}")
    public static String parameter(String url, String key) {
        return parameterMap(url).get(key);
    }

    @Comment("获取 url 全部参数")
    @Example("${url.parameterMap('https://www.baidu.com/s?wd=spider-flow&abbr=sf')}")
    public static Map<String, String> parameterMap(String url) {
        Map<String, String> map = new HashMap<String, String>();
        int index = url.indexOf("?");
        if (index != -1) {
            String param = url.substring(index + 1);
            if (StrUtil.isNotBlank(param)) {
                String[] params = param.split("&");
                for (String item : params) {
                    String[] kv = item.split("=");
                    if (kv.length > 0) {
                        if (StrUtil.isNotBlank(kv[0])) {
                            String value = "";
                            if (kv.length > 1 && StrUtil.isNotBlank(kv[1])) {
                                int kv1Index = kv[1].indexOf("#");
                                if (kv1Index != -1) {
                                    value = kv[1].substring(0, kv1Index);
                                } else {
                                    value = kv[1];
                                }
                            }
                            map.put(kv[0], value);
                        }
                    }
                }
            }
        }
        return map;
    }

    @Comment("url 编码")
    @Example("${url.encode('https://www.baidu.com/s?wd=spider-flow')}")
    public static String encode(String url) {
        return encode(url, Charset.defaultCharset().name());
    }

    @Comment("url 编码")
    @Example("${url.encode('https://www.baidu.com/s?wd=spider-flow','UTF-8')}")
    public static String encode(String url, String charset) {
        try {
            return url != null ? URLEncoder.encode(url, charset) : null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Comment("url 解码")
    @Example("${url.decode(strVar)}")
    public static String decode(String url) {
        return decode(url, Charset.defaultCharset().name());
    }

    @Comment("url 解码")
    @Example("${url.decode(strVar,'UTF-8')}")
    public static String decode(String url, String charset) {
        try {
            return url != null ? URLDecoder.decode(url, charset) : null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
