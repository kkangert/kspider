package top.kangert.kspider.executor.function;

import org.springframework.stereotype.Component;

import cn.hutool.core.util.RandomUtil;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;

/**
 * 随机数/字符串
 */
@Component
public class RandomFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "random";
    }

    @Comment("随机获取 int")
    @Example("${random.randomInt(1,10)}")
    public static int randomInt(int min, int max) {
        return RandomUtil.randomInt(min, max);
    }

    @Comment("随机获取 double")
    @Example("${random.randomDouble(1,10)}")
    public static double randomDouble(double min, double max) {
        return RandomUtil.randomDouble(min, max);
    }

    @Comment("随机获取 long")
    @Example("${random.randomLong(1,10)}")
    public static long randomLong(long min, long max) {
        return RandomUtil.randomLong(min, max);
    }

    /**
     * @param chars  字符个数
     * @param length 字符范围
     * @return String 随机字符串
     */
    @Comment("随机获取字符串")
    @Example("${random.string('abc',10)}")
    public static String string(String chars, int length) {
        if (chars != null) {
            char[] newChars = new char[length];
            int len = chars.length();
            for (int i = 0; i < length; i++) {
                newChars[i] = chars.charAt(randomInt(0, len));
            }
            return new String(newChars);
        }
        return null;
    }

}
