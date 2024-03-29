/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.web;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by mpostelnicu on 7/4/17.
 */
@SpringBootApplication
@PropertySource("classpath:/org/devgateway/jocds/web/application.properties")
@ComponentScan("org.devgateway.jocds")
public class ValidatorWebApplication {
    public static void main(final String[] args) {
        SpringApplication app = new SpringApplication(ValidatorWebApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}
