package top.kangert.kspider.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.kangert.kspider.entity.User;
import top.kangert.kspider.service.UserService;
import top.kangert.kspider.util.BaseResponse;
import top.kangert.kspider.util.PageInfo;

import java.util.Map;

import javax.annotation.Resource;

/**
 * 用户身份验证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public BaseResponse login(@Validated @RequestBody User user) {
        userService.login(request, response, user);
        return successResponse();
    }

    @GetMapping("/info")
    public BaseResponse userInfo(@RequestBody Map<String, Object> params) {
        PageInfo<User> userPage = userService.findUser(params);
        return successResponse(userPage);
    }
}
