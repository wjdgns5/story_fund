package com.storyfund.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // API 기본 정보
        return new OpenAPI()
                // Swagger UI 상단에 표시
                .info(new Info()
                        .title("StoryFund API") // 제목 입력
                        .description("유료 게시판 플랫폼 API 문서") // 설명
                        .version("v1.0.0")
                )

                /**
                 * Swagger UI 오른쪽 상단에 Authorize 버튼이 생겨요.
                 * 거기에 Access Token 한 번만 입력하면
                 * 모든 API 요청에 자동으로 Authorization: Bearer 토큰 이 붙어요.
                 */
                // JWT 인증 설정
                .addSecurityItem(new SecurityRequirement() // 전역 인증 적용
                        .addList("Bearer Authentication")
                )

                .components(new Components() // 인증 방식 정의
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("로그인 후 발급받은 Access Token 을 입력해주세요.")
                        )
                );
    } // end of openAPI()
}
