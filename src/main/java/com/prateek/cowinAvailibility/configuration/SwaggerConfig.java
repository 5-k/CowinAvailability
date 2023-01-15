package com.prateek.cowinAvailibility.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket swaggerSpringMvcPluginForBusinessCustomer() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.apiInfo(metaData()).groupName("AuthorizedAccess").select()
                .apis(RequestHandlerSelectors.basePackage("com.prateek.cowinAvailibility.controller.secured"))
                .paths(PathSelectors.any()).build().securitySchemes(Arrays.asList(basicAuthScheme()))
                .securityContexts(Collections.singletonList(basicSecurityContext()));
        return docket;
    }

    @Bean
    public Docket PublicAccessAPI() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.apiInfo(metaData()).groupName("PublicAccess").select()
                .apis(RequestHandlerSelectors.basePackage("com.prateek.cowinAvailibility.controller.publicAPI"))
                .paths(PathSelectors.any()).build();
        ;

        return docket;

    }

    private ApiInfo metaData() {

        ApiInfo apiInfo = new ApiInfo("Cowin Availability Notifier Application",
                "This application is a alert generating application. In India, vaccination trials require booking slots via https://cowin.gov.in . But due to a large demand, usually the vaccines are unavailable. This applicattion help users to create alerts. The alerts created are monitored using a cron job and the user is notified on availability.",
                "2.0", "https://github.com/5-k/CowinAvailability",
                new Contact("Prateek Mishra", "https://www.prateekmishra.me", "prateek15mishra@gmail.com"),
                "Apache License Version 2.0", "https://www.apache.org/licenses/LICENSE-2.0", new ArrayList<>());
        return apiInfo;
    }

    private SecurityScheme basicAuthScheme() {
        return new BasicAuth("basicAuth");
    }

    private SecurityContext basicSecurityContext() {
        return SecurityContext.builder()
                .securityReferences(
                        Collections.singletonList(new SecurityReference("basicAuth", new AuthorizationScope[0])))
                .build();
    }

}
