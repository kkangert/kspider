package top.kangert.kspider.util;

import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.support.UserAgentManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

/**
 * 文件处理工具类
 */
@Slf4j
@Component
public class FileUtils {

    @Autowired
    private static UserAgentManager userAgentManager;

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的 byte 数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 文件下载状态
     */
    public enum DownloadStatus {
        URL_ERROR(1, "URL 错误"),
        FILE_EXIST(2, "文件存在"),
        TIME_OUT(3, "连接超时"),
        DOWNLOAD_FAIL(4, "下载失败"),
        DOWNLOAD_SUCCESS(5, "下载成功");

        private int code;

        private String name;

        DownloadStatus(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static DownloadStatus downloadFile(String savePath, String url, String proxy, boolean downNew,
            boolean saveOriginPath) {
        URL fileUrl = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        if (url.startsWith("//")) {
            url = "http:" + url;
        }
        String fileName;
        try {
            fileUrl = new URL(url);
            String urlPath = fileUrl.getPath();

            if (saveOriginPath) {
                fileName = urlPath;
            } else {
                fileName = urlPath.substring(urlPath.lastIndexOf("/") + 1);
            }

        } catch (MalformedURLException e) {
            log.error("URL 异常", e);
            return DownloadStatus.URL_ERROR;
        }
        File path = new File(savePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(savePath + File.separator + fileName);
        if (file.exists()) {
            if (downNew) {
                file.delete();
            } else {
                log.info("文件已存在，不重新下载");
                return DownloadStatus.FILE_EXIST;
            }
        } else if (!file.getParentFile().exists() && saveOriginPath) {
            file.getParentFile().mkdirs();
        }
        try {
            if (StrUtil.isNotBlank(proxy)) {
                List<String> proxyArr = StrUtil.split(Constants.PROXY_HOST_PORT_SEPARATOR, proxy);
                if (proxyArr.size() == 2) {
                    InetSocketAddress socketAddress = new InetSocketAddress(proxyArr.get(0),
                            Integer.parseInt(proxyArr.get(1)));
                    Proxy p = new Proxy(Proxy.Type.HTTP, socketAddress);
                    httpUrl = (HttpURLConnection) fileUrl.openConnection(p);
                    log.info("设置下载代理：{}:{}", proxyArr.get(0), proxyArr.get(1));
                }
            } else {
                httpUrl = (HttpURLConnection) fileUrl.openConnection();
            }
            httpUrl.setRequestProperty("User-Agent", userAgentManager.getChromeNewest());
            // 读取超时时间
            httpUrl.setReadTimeout(60000);
            // 连接超时时间
            httpUrl.setConnectTimeout(60000);
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(file));
            final int len = 2048;
            byte[] buf = new byte[len];
            int readLen;
            while ((readLen = bis.read(buf)) != -1) {
                bos.write(buf, 0, readLen);
            }
            log.info("远程文件下载成功：" + url);
            bos.flush();
            bis.close();
            httpUrl.disconnect();
            return DownloadStatus.DOWNLOAD_SUCCESS;
        } catch (SocketTimeoutException e) {
            log.error("读取文件超时", e);
            return DownloadStatus.TIME_OUT;
        } catch (Exception e) {
            log.error("远程文件下载失败", e);
            return DownloadStatus.DOWNLOAD_FAIL;
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                log.error("下载出错", e);
            }
        }
    }

}
