package com.base.config;

/**
 * Created by Toyin on 2/28/19.
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig{
    @Bean
    public Docket produceApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.base.controller"))
                .paths(PathSelectors.any())
                .build().securitySchemes(Collections.singletonList(apiKey()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Countries Rest APIs")
                .description("This page lists all the rest apis for Country Management App.")
                .version("1.0-SNAPSHOT")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer %token", "Authorization", "Header");
    }
}
