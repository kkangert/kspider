package top.kangert.kspider.io;

import cn.hutool.json.JSONUtil;
import top.kangert.kspider.io.SpiderResponse;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.InputStream;
import java.util.Map;

/**
 * 响应对象包装类
 */
public class HttpResponse implements SpiderResponse {

    /**
     * 响应
     */
    private Response response;

    /**
     * 状态码
     */
    private int statusCode;

    /**
     * URL
     */
    private String urlLink;

    /**
     * 响应的 HTML 内容
     */
    private String htmlValue;

    /**
     * 响应的标题
     */
    private String title;

    /**
     * 响应的 json 内容
     */
    private Object jsonValue;

    public HttpResponse(Response response) {
        this.response = response;
        this.statusCode = response.statusCode();
        this.urlLink = response.url().toExternalForm();
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getTitle() {
        if (title == null) {
            synchronized (this) {
                title = Jsoup.parse(getHtml()).title();
            }
        }
        return title;
    }

    @Override
    public String getHtml() {
        if (htmlValue == null) {
            synchronized (this) {
                htmlValue = response.body();
            }
        }
        return htmlValue;
    }

    @Override
    public Object getJson() {
        if (jsonValue == null) {
            jsonValue = JSONUtil.parseObj(getHtml());
        }
        return jsonValue;
    }

    @Override
    public Map<String, String> getCookies() {
        return response.cookies();
    }

    @Override
    public Map<String, String> getHeaders() {
        return response.headers();
    }

    @Override
    public byte[] getBytes() {
        return response.bodyAsBytes();
    }

    @Override
    public String getContentType() {
        return response.contentType();
    }

    @Override
    public void setCharset(String charset) {
        this.response.charset(charset);
    }

    @Override
    public String getUrl() {
        return urlLink;
    }

    @Override
    public InputStream getStream() {
        return response.bodyStream();
    }

}
