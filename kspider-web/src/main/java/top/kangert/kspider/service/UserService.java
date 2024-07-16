package top.kangert.kspider.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import top.kangert.kspider.entity.User;
import top.kangert.kspider.util.PageInfo;

public interface UserService {

    PageInfo<User> findUser(Map<String,Object> params);

    void login(HttpServletRequest request,HttpServletResponse response,User user);

    void add(User user);

    void delete(Integer id);
}
