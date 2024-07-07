package top.kangert.kspider.executor.function;

import org.springframework.stereotype.Component;

import cn.hutool.crypto.digest.DigestUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;

import java.io.IOException;
import java.io.InputStream;

@Component
@Comment("MD5 常用方法")
public class MD5FunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "md5";
    }

    @Comment("md5 加密")
    @Example("${md5.string(resp.html)}")
    public static String string(String str) {
        return DigestUtil.md5Hex(str);
    }

    @Comment("md5 加密")
    @Example("${md5.string(resp.bytes)}")
    public static String string(byte[] bytes) {
        return DigestUtil.md5Hex(bytes);
    }

    @Comment("md5 加密")
    @Example("${md5.string(resp.stream)}")
    public static String string(InputStream stream) throws IOException {
        return DigestUtil.md5Hex(stream);
    }

    @Comment("md5 加密")
    @Example("${md5.bytes(resp.html)}")
    public static byte[] bytes(String str) {
        return DigestUtil.md5(str);
    }

    @Comment("md5 加密")
    @Example("${md5.bytes(resp.bytes)}")
    public static byte[] bytes(byte[] bytes) {
        return DigestUtil.md5(bytes);
    }

    @Comment("md5 加密")
    @Example("${md5.bytes(resp.stream)}")
    public static byte[] bytes(InputStream stream) throws IOException {
        return DigestUtil.md5(stream);
    }
}
