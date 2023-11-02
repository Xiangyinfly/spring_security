package com.xiang.config;

import com.xiang.exception.LoginFailureHandler;
import com.xiang.filter.LoginFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
//@EnableWebSecurity：开启SpringSecurity之后会默认注册大量的过滤器servlet filter
//过滤器链【责任链模式】securityFilterChain
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //authorizeHttpRequests:针对http请求进行授权配置

        //Login登录页面需要匿名访问
        //permitAll：具有所有权限也就可以匿名可以访问
        //anyRequest():所有请求
        //authenticated()：需要登录
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        //用户
//                .requestMatchers("/admin/api").hasRole("admin")
//                .requestMatchers("/user/api").hasAnyRole("admin","user")

                        //权限
                        .requestMatchers("/admin/api").hasAuthority("admin:api")
                        .requestMatchers("/user/api").hasAnyAuthority("admin:api", "user:api")

                        //匹配模式
                        //.requestMatchers("/admin/api/?").hasAuthority("admin:api")

                        .requestMatchers("/app/api").permitAll()
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
        );

        //处理未授权页面
        //http.exceptionHandling(e -> e.accessDeniedPage("/noAuth"));
        //未授权异常
        http.exceptionHandling(e->e.accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                System.out.println("accessDeniedException = " + accessDeniedException);
                accessDeniedException.printStackTrace();
            }
        }));

        //http：后面可以一直点但是太多内容之后不美观
        //LoginPage：登录页面
        //LoginProcessingUrL：登录接口过滤器
        //defaultSuccessUrl:登录成功之后跳转的页面
        http.formLogin(formLogin -> formLogin
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login")
                .failureHandler(new LoginFailureHandler())
                .defaultSuccessUrl("/index")
        );

        //设置cookie中的remember-me
        //rememberMeParameter：表单中的名字
        //rememberMeCookieName：cookie中的名字
        http.rememberMe(e->e.rememberMeParameter("remember-me").rememberMeCookieName("remember-me").key("mykey"));

        //添加自定义过滤器，将UsernamePasswordAuthenticationFilter替换掉
        //http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);

        //csrf:跨域漏洞防御：关闭
        http.csrf(Customizer.withDefaults());
        //http.csrf(csrf->csrf.disable());

        //退出
        http.logout(logout -> logout.invalidateHttpSession(true));

        return http.build();
    }

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationFailureHandler(new LoginFailureHandler());
        loginFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        return loginFilter;
    }

//    //配置用户
//    @Bean
//    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
////        UserDetails user1 = User.withUsername("admin").password("123456").roles("user","admin").authorities("admin:api","user:api").build();
////        UserDetails user2 = User.withUsername("user").password("123456").roles("user").authorities("user:api").build();
//        UserDetails user1 = User.withUsername("admin").password("123456").authorities("admin:api", "user:api").build();
//        UserDetails user2 = User.withUsername("user").password("123456").authorities("user:api").build();
//        return new InMemoryUserDetailsManager(user1, user2);
//    }

    //配置密码
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
