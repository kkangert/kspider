package top.kangert.kspider.config;

import lombok.extern.slf4j.Slf4j;
import top.kangert.kspider.entity.Application;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.exception.ExceptionCodes;
import top.kangert.kspider.repository.ApplicationRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    @Autowired
    private ApplicationRepository applicationRepository;

    class TokenAuthFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            // 获取Cookie
            Cookie[] cookies = request.getCookies();
            if (ObjectUtil.isEmpty(cookies)) {
                errorInfo(response, ExceptionCodes.TOKEN_NON_EXISTENT);
                return;
            }
            // 获取token
            String token = null;
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }

            String tokenAuth = (String) request.getSession().getAttribute("token");
            if (StrUtil.isBlank(tokenAuth)) {
                String appSecretKey = request.getHeader("appSecretKey");
                if (StrUtil.isBlank(appSecretKey)) {
                    errorInfo(response, ExceptionCodes.TOKEN_NON_EXISTENT);
                    return;
                }
                Application application = applicationRepository.findByAppSecretKey(appSecretKey);
                if (null == application) {
                    errorInfo(response, ExceptionCodes.KEY_ERROR);
                    return;
                }
            } else if (!tokenAuth.equals(token)) {
                errorInfo(response, ExceptionCodes.TOKEN_NON_EXISTENT);
                return;
            }
            String username = (String) request.getSession().getAttribute("username");
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("admin"));
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                    tokenAuth, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        }

    }

    /**
     * 异常信息输出
     * 
     * @param response
     * @param tokenNonExistent
     */
    private void errorInfo(HttpServletResponse response, ExceptionCodes tokenNonExistent) {
        errorInfo(response, new BaseException(tokenNonExistent));
    }

    /**
     * 异常信息输出
     * 
     * @param response
     * @param exception
     */
    private void errorInfo(HttpServletResponse response, BaseException exception) {
        // 验证错误信息
        Map<String, Object> errorInfo = new HashMap<String, Object>();
        errorInfo.put("code", exception.getCode());
        errorInfo.put("message", exception.getMessage());
        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(JSONUtil.parseObj(errorInfo).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                // 开启session管理
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .authorizeRequests()

                // 登录请求不拦截
                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()

                // 允许 websocket 请求
                .antMatchers("/ws").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(new TokenAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return new WebSecurityCustomizer() {

            @Override
            public void customize(WebSecurity web) {
                web.ignoring().antMatchers("/auth/login");

                web.ignoring().antMatchers(HttpMethod.GET, "/");
                web.ignoring().antMatchers(HttpMethod.GET, "/assets/*");
            }
        };
    }

}
