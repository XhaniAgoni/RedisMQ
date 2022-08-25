package com.example.redisservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
//http://localhost:6381/swagger-ui.htm
public class RedisSwaggerConfiguration {
//配置Swagger的Bean实例

    @Bean

    public Docket swaggerSpringMvcPlugin() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true)//enable表示是否开启Swagger
                .select()
            //RequestHandlerSelectors指定扫描的包
                .apis(RequestHandlerSelectors.basePackage("com.example.redisservice.controller"))
                .build();
    }

    //配置API的基本信息（会在http://项目实际地址/swagger-ui.html页面显示）
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("redisAPI文档")
                .description("redisApi接口文档描述")
                .termsOfServiceUrl("http://127.0.0.1:6381")
                .version("2.0")
                .build();
    }
}

