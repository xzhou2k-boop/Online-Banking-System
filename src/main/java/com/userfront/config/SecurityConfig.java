package com.userfront.config;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.userfront.service.UserServiceImpl.UserSecurityService;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Autowired
    private UserSecurityService userSecurityService;

    private static final String SALT = "salt"; // Salt should be protected carefully


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12, new SecureRandom(SALT.getBytes()));
    }

    private static final String[] PUBLIC_MATCHERS = {
            "/webjars/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/",
            "/about/**",
            "/contact/**",
            "/error/**/*",
            "/console/**",
            "/signup"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .httpBasic()
                .and()
                .authorizeRequests()
                    .antMatchers(PUBLIC_MATCHERS).permitAll()
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/api/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");

                        Map<String, Object> result = new HashMap<>();
                        result.put("message", "权限不足，无法访问该资源");
                        result.put("path", request.getRequestURI());

                        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                    })
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");

                        Map<String, Object> result = new HashMap<>();
                        result.put("message", "未认证，请先登录");
                        result.put("path", request.getRequestURI());

                        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                     })
                .and()
                .csrf()
                    .ignoringAntMatchers("/h2-console/**")
                    .disable()
                .cors().disable()
                .formLogin()
                    .loginPage("/index")
                    .loginProcessingUrl("/index")
                    .failureUrl("/index?error")
                    .defaultSuccessUrl("/userFront",true)
                    .permitAll()
                .and()
                .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/index?logout")
                    .deleteCookies("remember-me")
                    .permitAll()
                .and()
                .rememberMe()
                    .rememberMeParameter("remember-me")
                    .key("uniqueAndSecretKey")
                    .tokenValiditySeconds(1209600)
                .and()
                .headers()
                    .frameOptions().sameOrigin();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());
    }

}
