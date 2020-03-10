package com.backbase.academy.peachtree;

import com.backbase.academy.peachtree.mapper.TransactionMapper;
import com.backbase.peachtree.mambu.config.MambuApiClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackageClasses = {MambuApiClientConfiguration.class},basePackages = "com.backbase.academy.peachtree")
public class Application extends SpringBootServletInitializer {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public TransactionMapper transactionMapper() {
        return TransactionMapper.MAPPER;
    }

    @Bean(value = "interServiceRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}