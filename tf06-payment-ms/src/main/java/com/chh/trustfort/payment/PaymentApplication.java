package com.chh.trustfort.payment;

import com.chh.trustfort.payment.component.TestEmailSender;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@OpenAPIDefinition(
        info = @Info(title = "Wallet API", version = "1.0", description = "API for Wallet System")
)
@SpringBootApplication(scanBasePackages = "com.chh.trustfort.payment")
@EntityScan("com.chh.trustfort.payment.model") // Ensure Hibernate finds AppUser entity
@Import(AppConfig.class)
@EnableHystrixDashboard
@EnableCircuitBreaker
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

    @Autowired
    private TestEmailSender testEmailSender;

    @PostConstruct
    public void runOnce() {
        testEmailSender.sendEmailManually();
    }


}
