package com.storyfund.api.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    private JavaMailSender mailSender;
    private RedisTemplate<String, String> redisTemplate; // Redis 와 통신하는 도구

    public EmailService(JavaMailSender mailSender, RedisTemplate<String, String> redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    // 6자리 난수 생성
    private String generateCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(code);
    }

    // 인증 코드 발송
    public void sendVerificationCode(String email) {

        // 1. 6자리 난수 발생
        String code = generateCode();

        // 2. Redis 에 저장 (key: 이메일, value: 코드, TTL: 5분)
        redisTemplate.opsForValue().set( // opsForValue() : 단순 key-value 저장 방식
                "email:" + email, // key
                code, // value
                5, // 시간
                TimeUnit.MINUTES // 단위 (분)
        );

        // 3. 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[StoryFund] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n\n5분 안에 입력해주세요.");
        mailSender.send(message);
    }

    // 인증 코드 확인
    public boolean verifyCode(String email, String code) {

        // 1. Redis 에서 저장된 코드 꺼내기
        // opsForValue() : 단순 key-value 저장 방식
        // Redis 에서 key 로 value 를 꺼내요. 5분이 지났으면 자동 삭제됐으니 null 이 반환돼요.
        String savedCode = redisTemplate.opsForValue().get("email:" + email);

        // 2. 저장된 코드가 없으면 (만료 또는 미발송)
        if (savedCode == null) {
            return false;
        }

        // 코드 일치 여부 확인
        return savedCode.equals(code);
    }


}
