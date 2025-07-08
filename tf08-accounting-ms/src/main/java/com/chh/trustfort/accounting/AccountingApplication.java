package com.chh.trustfort.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(basePackages = {
        "com.chh.trustfort.accounting.model",
        "com.chh.trustfort.accounting.component"
})
@SpringBootApplication(scanBasePackages = "com.chh.trustfort.accounting")
@Import(AppConfig.class)
@EnableHystrixDashboard
@EnableCircuitBreaker
@EnableEurekaClient
@EnableFeignClients
@EnableScheduling
public class AccountingApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountingApplication.class, args);
    }
}
