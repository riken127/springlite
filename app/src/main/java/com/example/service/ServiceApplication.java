package com.example.service;

import com.example.service.bootstrap.ApplicationConfiguration;
import com.example.service.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/** Process entry point with an explicit, AOT-friendly application context. */
@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration
@EnableConfigurationProperties(AppProperties.class)
@Import(ApplicationConfiguration.class)
public class ServiceApplication {
  /** Starts the Spring application. */
  public static void main(String[] args) {
    SpringApplication.run(ServiceApplication.class, args);
  }
}
