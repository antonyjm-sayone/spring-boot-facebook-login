package com.opensource.facebooklogin.config;

import com.opensource.facebooklogin.exception.RestAuthenticationEntryPoint;
import com.opensource.facebooklogin.service.TokenProvider;
import com.opensource.facebooklogin.token.TokenAuthenticationFilter;
import com.opensource.facebooklogin.service.CustomUserDetailsService;
import com.opensource.facebooklogin.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.security.SecureRandom;


@Configuration
public class SecurityConfig {
    //public class SecurityConfig extends  WebSecurityConfigurerAdapter{

    @Configuration
    public class OAuthConfig extends WebSecurityConfigurerAdapter{
        @Autowired
        private CustomUserDetailsService userDetailsService;


        @Override
        public void configure(HttpSecurity http)throws Exception{

            http
                    .cors()
                        .and()
                    .httpBasic()
                        .disable()
                    .userDetailsService(userDetailsService)
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                    .csrf()
                        .disable()
                    .formLogin()
                        .disable()
                    .authorizeRequests()
                        .antMatchers("/login/**", "/custom-oauth/**")
                            .permitAll()
                        .anyRequest()
                            .authenticated()
                    .and()
                    .oauth2Login();

        }
    }

    @Configuration
    @Order(1)
    public class ApiConfig extends WebSecurityConfigurerAdapter{

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Autowired
        private TokenProvider tokenProvider;

        @Autowired
        private CookieUtil cookieUtil;

        @Override
        public void configure(HttpSecurity http) throws Exception{
            http
                    .cors()
                        .and()
                    .httpBasic()
                        .disable()
                    .exceptionHandling()
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .and()
                    .addFilter(new TokenAuthenticationFilter(authenticationManager(), tokenProvider, cookieUtil))
                    .userDetailsService(userDetailsService)
                    .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()
                    .csrf()
                        .disable()
                    .formLogin()
                        .disable()
                    .antMatcher("/api/**")
                        .authorizeRequests()
                        .anyRequest()
                        .authenticated();
//                        .and()
//                    .oauth2Login();

        }

    }

//    @Override
//    public void configure(HttpSecurity http) throws Exception{
//        http
//                .cors()
//                .and()
//                .httpBasic()
//                    .disable()
//                .exceptionHandling()
//                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
//                    .and()
//                .addFilter(new TokenAuthenticationFilter(authenticationManager(), tokenProvider, cookieUtil))
//                .userDetailsService(userDetailsService)
//                .sessionManagement()
//                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                    .and()
//                .csrf()
////                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
////                    .and()
//                .disable()
//                .formLogin()
//                    .disable()
//                .authorizeRequests()
//                    .antMatchers("/login/**", "/custom-oauth/**", "/oauth2/**")
//                        .permitAll()
//                    .anyRequest()
//                        .authenticated()
//                    .and()
//                .oauth2Login();
//
//    }

}
