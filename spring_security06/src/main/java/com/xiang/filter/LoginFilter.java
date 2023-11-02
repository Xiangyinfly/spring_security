package com.xiang.filter;

import cn.hutool.json.JSONUtil;
import com.xiang.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    //private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "POST");
    //继承了UsernamePasswordAuthenticationFilter类，默认登录路径就是/login
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(request.getMethod());
        }

        BufferedReader reader = request.getReader();
        StringBuffer sbf = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sbf.append(line);
        }
        User user = JSONUtil.parseObj(sbf.toString()).toBean(User.class);

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), user.getPassword());

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
