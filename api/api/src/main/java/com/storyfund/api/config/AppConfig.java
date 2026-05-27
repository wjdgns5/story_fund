package com.storyfund.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 클래스 설정 파일
public class AppConfig {

    @Bean // 객체를 Spring 에서 관리
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
