package com.cloudclassroom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 配置。
 */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI cloudClassroomOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("云课堂 CloudClassroom API")
            .description("云课堂（CloudClassroom）后端接口文档")
            .version("0.1.0"));
  }
}
