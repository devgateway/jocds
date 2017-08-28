package org.devgateway.jocds.web;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

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
