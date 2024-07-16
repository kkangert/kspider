package top.kangert.kspider.util;

import org.springframework.security.core.context.SecurityContextHolder;

import top.kangert.kspider.entity.User;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.exception.ExceptionCodes;

/**
 * 自定义Security Context Holder
 */
public class MySecurityContextHolder {

    public static User getCurrentUser() {
        User user;
        try {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new BaseException(ExceptionCodes.ERROR, "用户未授权");
        }
        return user;
    }
}
