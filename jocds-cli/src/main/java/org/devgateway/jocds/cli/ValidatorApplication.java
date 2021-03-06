/*
 * Copyright (c) 2017. Development Gateway and contributors. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package org.devgateway.jocds.cli;

import java.util.Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by mpostelnicu on 7/4/17.
 */
@SpringBootApplication
@PropertySource("classpath:/org/devgateway/jocds/cli/application.properties")
@ComponentScan("org.devgateway.jocds")
public class ValidatorApplication {
    public static void main(final String[] args) {
        SpringApplication app = new SpringApplication(ValidatorApplication.class);
        app.setLogStartupInfo(false);
        Properties properties = new Properties();
        properties.setProperty("logging.level.root", "WARN");
        app.setDefaultProperties(properties);
        app.run(args);
    }
}
