package com.xiang.filter;

import com.xiang.exception.VerificationCodeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class VerificationCodeFilter extends OncePerRequestFilter {

    //验证码异常跳转到/login
    AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationFailureHandler() {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            log.info("异常:{}",exception.getMessage());
            response.sendRedirect("/login");
        }
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!("/login".equals(request.getRequestURI()) && request.getMethod().equals("POST"))){//login页面不走验证码
            filterChain.doFilter(request,response);
        }else{
            try {
                verificationCode(request,response);
                filterChain.doFilter(request,response);
            } catch (VerificationCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(request,response,e);
            }

        }
    }

    public void verificationCode(HttpServletRequest request, HttpServletResponse response) throws VerificationCodeException {
        String requestCode = request.getParameter("captcha");
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("captcha");
        if(!StringUtils.isEmpty(sessionCode)){
            session.removeAttribute("captcha");}

        if(StringUtils.isEmpty(requestCode) || StringUtils.isEmpty(sessionCode) || !requestCode.equals(sessionCode)){
            throw new VerificationCodeException();
        }

    }

}
