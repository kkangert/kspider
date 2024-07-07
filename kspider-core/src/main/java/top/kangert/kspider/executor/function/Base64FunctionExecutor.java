package top.kangert.kspider.executor.function;

import org.springframework.stereotype.Component;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;

/**
 * 字符串内容和 Base64 互相转换
 */
@Component
@Comment("base64 常用方法")
public class Base64FunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "base64";
    }

    @Comment("根据 byte[] 进行 base64 编码")
    @Example("${base64.encode(resp.bytes)}")
    public static String encode(byte[] bytes) {
        return bytes != null ? Base64Encoder.encode(bytes) : null;
    }

    @Comment("根据 byte[] 进行 base64 编码")
    @Example("${base64.encode(resp.bytes,'UTF-8')}")
    public static String encode(String content, String charset) {
        return encode(StringFunctionExecutor.bytes(content, charset));
    }

    @Comment("根据 String 进行 base64 编码")
    @Example("${base64.encode(resp.html)}")
    public static String encode(String content) {
        return encode(StringFunctionExecutor.bytes(content));
    }

    @Comment("根据 byte[] 进行 base64 编码")
    @Example("${base64.encodeBytes(resp.bytes)}")
    public static byte[] encodeBytes(byte[] bytes) {
        return bytes != null ? Base64Encoder.encode(bytes, false) : null;
    }

    @Comment("根据 String 进行 base64 编码")
    @Example("${base64.encodeBytes(resp.html,'UTF-8')}")
    public static byte[] encodeBytes(String content, String charset) {
        return encodeBytes(StringFunctionExecutor.bytes(content, charset));
    }

    @Comment("根据 String 进行 base64 编码")
    @Example("${base64.encodeBytes(resp.html)}")
    public static byte[] encodeBytes(String content) {
        return encodeBytes(StringFunctionExecutor.bytes(content));
    }

    @Comment("根据 String 进行 base64 解码")
    @Example("${base64.decode(resp.html)}")
    public static byte[] decode(String base64) {
        return base64 != null ? Base64Decoder.decode(base64) : null;
    }

    @Comment("根据 byte[] 进行 base64 解码")
    @Example("${base64.decode(resp.bytes)}")
    public static byte[] decode(byte[] base64) {
        return base64 != null ? Base64Decoder.decode(base64) : null;
    }

    @Comment("根据 String 进行 base64 解码")
    @Example("${base64.decodeString(resp.html)}")
    public static String decodeString(String base64) {
        return base64 != null ? new String(Base64Decoder.decode(base64)) : null;
    }

    @Comment("根据 byte[] 进行 base64 解码")
    @Example("${base64.decodeString(resp.bytes)}")
    public static String decodeString(byte[] base64) {
        return base64 != null ? new String(Base64Decoder.decode(base64)) : null;
    }

    @Comment("根据 byte[] 进行 base64 解码")
    @Example("${base64.decodeString(resp.bytes,'UTF-8')}")
    public static String decodeString(byte[] base64, String charset) {
        return base64 != null ? StringFunctionExecutor.newString(Base64Decoder.decode(base64), charset) : null;
    }
}
