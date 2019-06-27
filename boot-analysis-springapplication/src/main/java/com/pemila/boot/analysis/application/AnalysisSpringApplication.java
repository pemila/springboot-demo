package com.pemila.boot.analysis.application;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 月在未央
 * @date 2019/6/27 11:04
 */
@SpringBootApplication
public class AnalysisSpringApplication {
    public static void main(String[] args) {
        /*
        SpringApplication.run(AnalysisSpringApplication.class,args);*/


        new SpringApplicationBuilder(AnalysisSpringApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE)
                .profiles("dev")
                .run(args);

        /*
        SpringApplication application = new SpringApplication(AnalysisSpringApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setAdditionalProfiles("dev");
        application.run(args);*/
    }
}
