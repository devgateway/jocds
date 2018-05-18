/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class ValidatorSwaggerConfig {

    @Bean
    public Docket validatorApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("jocds").apiInfo(validatorApiInfo())
                .select().apis(RequestHandlerSelectors.any()).paths(regex("/api/.*")).build();
    }

    private ApiInfo validatorApiInfo() {
        return new ApiInfoBuilder().title("jOCDS - The Open Contracting Data Standard (OCDS) Java Validator")
                .description("Validates <a href='http://standard.open-contracting.org/'>OCDS</a> JSON resources, "
                        + "resource packages, URLs, with or without extensions.<br/>"
                        + "<a href='https://github.com/devgateway/jocds'>Source Code & Documentation</a><br/>"
                        + "<a href='https://bintray.com/devgateway/jocds/jocds'>Download latest version from Bintray"
                        + "</a>")
                .license("jOCDS is open-source software released under the MIT license.")
                .version("0.3.1")
                .contact(new Contact("Mihai Postelnicu", "http://developmentgateway.org",
                        "mpostelnicu@developmentgateway.org"))
                .termsOfServiceUrl("http://www.developmentgateway.org/privacy-and-terms")
                .licenseUrl("https://opensource.org/licenses/MIT").build();
    }

}