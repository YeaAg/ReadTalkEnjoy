package com.project.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private DataSource dataSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configure -> {
            // ✅ 관리자 페이지 보호: "ADMIN" 역할이 있어야만 접근 가능
            configure.requestMatchers("/admin/**").hasRole("ADMIN");

            // ✅ 공개 접근 허용 경로
            configure.requestMatchers("/static/**", "/", "/book/**", "/content/**").permitAll();
            configure.requestMatchers("/mail/**", "/user/email/**", "/user/email/auth/**").permitAll();
            configure.requestMatchers("/complain", "/user/join", "/discussion/category", "/discussion/category/search",
                    "/user/complain", "/user/find-id", "/user/findId/**", "/user/find-id",
                    "/user/id/**", "/user/info", "/user/info-revise", "/user/login", "/user/pw-auth",
                    "/user/resetPw/", "/user/resetPw/password", "/user/tel/", "/user/tel/auth").permitAll();

            // ✅ 그 외 모든 요청은 인증 필요
            configure.anyRequest().authenticated();
        });

        http.userDetailsService(userDetailsService)
                .formLogin(Customizer.withDefaults());

        http.formLogin(configure -> {
            configure.loginPage("/user/login")
                    .loginProcessingUrl("/user/login")
                    .usernameParameter("id")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/")
                    .permitAll();
        });

        http.logout(configure -> {
            configure.logoutUrl("/user/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/");
        });

        http.oauth2Login(configure -> {
            configure.loginPage("/user/login")
                    .failureUrl("/user/join")
                    .defaultSuccessUrl("/")
                    .permitAll();
        });

        http.rememberMe(configure -> {
            configure.userDetailsService(userDetailsService)
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(60 * 10);
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
}
