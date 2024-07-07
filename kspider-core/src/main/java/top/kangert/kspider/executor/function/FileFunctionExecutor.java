package top.kangert.kspider.executor.function;

import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;
import top.kangert.kspider.util.FileUtils;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文件读写
 */
@Component
@Comment("file 常用方法")
public class FileFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "file";
    }

    /**
     * @param path            文件路径/名
     * @param createDirectory 是否需要创建
     * @return File 文件
     */
    private static File getFile(String path, boolean createDirectory) {
        File f = new File(path);
        if (createDirectory && !f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        return f;
    }

    @Comment("写出文件")
    @Example("${file.write('e:/result.html',resp.html,false)}")
    public static void write(String path, String content, boolean append) throws IOException {
        write(path, content, Charset.defaultCharset().name(), append);
    }

    @Comment("写出文件")
    @Example("${file.write('e:/result.html',resp.html,'UTF-8',false)}")
    public static void write(String path, String content, String charset, boolean append) throws IOException {
        write(path, StringFunctionExecutor.bytes(content, charset), append);
    }

    @Comment("写出文件")
    @Example("${file.write('e:/result.html',resp.bytes,false)}")
    public static void write(String path, byte[] bytes, boolean append) throws IOException {
        write(path, new ByteArrayInputStream(bytes), append);
    }

    @Comment("写出文件")
    @Example("${file.write('e:/result.html',resp.stream,false)}")
    public static void write(String path, InputStream stream, boolean append) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(getFile(path, true), append)) {
            IoUtil.copy(stream, fos);
        }
    }

    @Comment("写出文件")
    @Example("${file.write('e:/result.html',resp.bytes,false)}")
    public static void write(String path, InputStream stream) throws IOException {
        write(path, stream, false);
    }

    @Comment("写出文件")
    @Example("${file.write('e:/result.html',resp.html)}")
    public static void write(String path, String content) throws IOException {
        write(path, content, false);
    }

    @Comment("写出文件")
    @Example("${file.write('e:/result.html',resp.html,'UTF-8')}")
    public static void write(String path, String content, String charset) throws IOException {
        write(path, content, charset, false);
    }

    @Comment("写出文件")
    @Example("${file.write('e:/result.html',resp.bytes)}")
    public static void write(String path, byte[] bytes) throws IOException {
        write(path, bytes, false);
    }

    @Comment("下载 Url 资源")
    @Example("${file.download('e:/downloadPath',urls)}")
    public static void download(String path, List<String> urls) throws IOException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                FileUtils.downloadFile(path, url, "", true, false);
            }
        }
    }

    @Comment("下载 Url 资源，是否保存原始文件路径")
    @Example("${file.download('e:/downloadPath',urls,false)}")
    public static void download(String path, List<String> urls, boolean saveOriginPath) throws IOException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                FileUtils.downloadFile(path, url, "", true, saveOriginPath);
            }
        }
    }

    @Comment("随机延时（单位毫秒）下载 Url 资源")
    @Example("${file.download('e:/downloadPath',urls,[1000,4000])}")
    public static void download(String path, List<String> urls, List<Integer> randomRange)
            throws IOException, InterruptedException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                Long sleepMillis = RandomUtil.randomLong(randomRange.get(0), randomRange.get(1));
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                FileUtils.downloadFile(path, url, "", false, false);
            }
        }
    }

    @Comment("随机延时（单位毫秒）下载 Url 资源")
    @Example("${file.download('e:/downloadPath',urls,[1000,4000],false)}")
    public static void download(String path, List<String> urls, List<Integer> randomRange, boolean saveOriginPath)
            throws IOException, InterruptedException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                Long sleepMillis = RandomUtil.randomLong(randomRange.get(0), randomRange.get(1));
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                FileUtils.downloadFile(path, url, "", false, saveOriginPath);
            }
        }
    }

    @Comment("通过代理下载 Url 资源")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',urls)}")
    public static void download(String path, String proxy, List<String> urls) throws IOException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                FileUtils.downloadFile(path, url, proxy, true, false);
            }
        }
    }

    @Comment("通过代理下载 Url 资源")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',urls,false)}")
    public static void download(String path, String proxy, List<String> urls, boolean saveOriginPath) throws IOException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                FileUtils.downloadFile(path, url, proxy, true, saveOriginPath);
            }
        }
    }

    @Comment("通过代理并使用随机延时（单位毫秒）来下载 Url 资源")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',urls,[1000,4000])}")
    public static void download(String path, String proxy, List<String> urls, List<Integer> randomRange)
            throws IOException, InterruptedException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                Long sleepMillis = RandomUtil.randomLong(randomRange.get(0), randomRange.get(1));
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                FileUtils.downloadFile(path, url, proxy, false, false);
            }
        }
    }

    @Comment("通过代理并使用随机延时（单位毫秒）来下载 Url 资源")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',urls,[1000,4000],false)}")
    public static void download(String path, String proxy, List<String> urls, List<Integer> randomRange, boolean saveOriginPath)
            throws IOException, InterruptedException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                Long sleepMillis = RandomUtil.randomLong(randomRange.get(0), randomRange.get(1));
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                FileUtils.downloadFile(path, url, proxy, false, saveOriginPath);
            }
        }
    }

    @Comment("下载 Url 资源")
    @Example("${file.download('e:/downloadPath',url)}")
    public static void download(String path, String url) throws IOException {
        if (url != null) {
            FileUtils.downloadFile(path, url, "", true, false);
        }
    }

    @Comment("下载 Url 资源")
    @Example("${file.download('e:/downloadPath',url,false)}")
    public static void download(String path, String url, boolean saveOriginPath) throws IOException {
        if (url != null) {
            FileUtils.downloadFile(path, url, "", true, saveOriginPath);
        }
    }

    @Comment("通过代理下载 Url 资源")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',url)}")
    public static void download(String path, String proxy, String url) throws IOException {
        if (url != null) {
            FileUtils.downloadFile(path, url, proxy, true, false);
        }
    }

    @Comment("通过代理下载 Url 资源")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',url,false)}")
    public static void download(String path, String proxy, String url, boolean saveOriginPath) throws IOException {
        if (url != null) {
            FileUtils.downloadFile(path, url, proxy, true, saveOriginPath);
        }
    }

    @Comment("读取文件")
    @Example("${file.bytes('e:/result.html')}")
    public static byte[] bytes(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(getFile(path, false))) {
            return IoUtil.readBytes(fis);
        }
    }

    @Comment("读取文件")
    @Example("${file.string('e:/result.html','UTF-8')}")
    public static String string(String path, String charset) throws IOException {
        return StringFunctionExecutor.newString(bytes(path), charset);
    }

    @Comment("读取文件")
    @Example("${file.string('e:/result.html')}")
    public static String string(String path) throws IOException {
        return StringFunctionExecutor.newString(bytes(path), Charset.defaultCharset().name());
    }

}
