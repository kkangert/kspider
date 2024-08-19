package top.kangert.kspider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import top.kangert.kspider.entity.User;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.exception.ExceptionCodes;
import top.kangert.kspider.repository.UserRepository;
import top.kangert.kspider.service.BaseService;
import top.kangert.kspider.service.UserService;
import top.kangert.kspider.util.PageInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl extends BaseService implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public PageInfo<User> findUser(Map<String,Object> params) {
        //获取分页配置
        Pageable pageCon = processPage(params);
        //获取分页信息
        Page<User> userPage = userRepository.findAll(multipleConditionsBuilder(params),pageCon);
        return new PageInfo<User>(userPage);
    }

    @Override
    public void login(HttpServletRequest request,HttpServletResponse response,User user) {
        User entity = userRepository.findByUsername(user.getUsername());
        if (Objects.isNull(entity) || !user.getPassword().equals(entity.getPassword())) {
            throw new BaseException(ExceptionCodes.USER_LOGIN_ERROR, "无效的用户名或密码");
        }
        //生成token
        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");
        String currentDate = simp.format(new Date());
        StringBuffer append = new StringBuffer(currentDate);
        append.append(user.getUsername() + "-" + user.getPassword());
        append.append(RandomUtil.randomDouble());
        String scrity = append.toString();
        String token = SecureUtil.md5(scrity);
        System.out.println(token);
        //存储token
        HttpSession session = request.getSession();
        session.setAttribute("token", token);
        session.setAttribute("username", user.getUsername());
        Cookie cookie = new Cookie("token",token);
        cookie.setPath("/");
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    @Override
    public void add(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }
    
}
