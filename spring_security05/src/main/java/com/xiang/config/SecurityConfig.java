package com.xiang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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
        http.exceptionHandling(e -> e.accessDeniedPage("/noAuth"));

        //http：后面可以一直点但是太多内容之后不美观
        //LoginPage：登录页面
        //LoginProcessingUrL：登录接口过滤器
        //defaultSuccessUrl:登录成功之后跳转的页面
        http.formLogin(formLogin -> formLogin
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index")
        );

        //csrf:跨域漏洞防御：关闭
        http.csrf(Customizer.withDefaults());
        //http.csrf(csrf->csrf.disable());

        //退出
        http.logout(logout -> logout.invalidateHttpSession(true));

        return http.build();
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
