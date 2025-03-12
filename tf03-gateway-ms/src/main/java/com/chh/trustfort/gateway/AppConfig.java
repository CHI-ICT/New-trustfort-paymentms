/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway;

import com.chh.trustfort.gateway.tenant.MultiTenantDataSource;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import java.lang.System.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import io.swagger.v3.oas.models.servers.Server;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

/**
 *
 * @author Daniel Ofoleta
 */
@Configuration
@ComponentScan
@EntityScan({"com.chh.trustfort.gateway.model"})
@PropertySource("classpath:application.yml")
@EnableTransactionManagement
@EnableWebMvc
@EnableEncryptableProperties
public class AppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    Environment env;
    protected Logger logger;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/images/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/js/");
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".xhtml");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.addDialect(new Java8TimeDialect());
        return templateEngine;
    }

    
    @Bean
    public DataSource multiTenantDataSource() {
        Map<Object, Object> dataSources = new HashMap<>();

        // Configure default tenants (this can be fetched dynamically from a registry)
        dataSources.put(env.getProperty("default-tenant.id"), MultiTenantDataSource.createDataSource(
                env.getProperty("default-tenant.url"), env.getProperty("default-tenant.db-user"), env.getProperty("default-tenant.db-pass")));
        dataSources.put(env.getProperty("chi-tenant.id"), MultiTenantDataSource.createDataSource(
                env.getProperty("chi-tenant.url"), env.getProperty("chi-tenant.db-user"), env.getProperty("chi-tenant.db-pass")));
        dataSources.put(env.getProperty("hmo-tenant.id"), MultiTenantDataSource.createDataSource(
                env.getProperty("hmo-tenant.url"), env.getProperty("hmo-tenant.db-user"), env.getProperty("hmo-tenant.db-pass")));
        dataSources.put(env.getProperty("cla-tenant.id"), MultiTenantDataSource.createDataSource(
                env.getProperty("cla-tenant.url"), env.getProperty("cla-tenant.db-user"), env.getProperty("cla-tenant.db-pass")));
        dataSources.put(env.getProperty("hfc-tenant.id"), MultiTenantDataSource.createDataSource(
                env.getProperty("hfc-tenant.url"), env.getProperty("hfc-tenant.db-user"), env.getProperty("hfc-tenant.db-pass")));
        dataSources.put(env.getProperty("chh-tenant.id"), MultiTenantDataSource.createDataSource(
                env.getProperty("chh-tenant.url"), env.getProperty("chh-tenant.db-user"), env.getProperty("chh-tenant.db-pass")));

        return new MultiTenantDataSource(dataSources);
    }

    @Bean()
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(multiTenantDataSource());
        em.setPackagesToScan(new String[]{"com.chh.trustfort.gateway.model", "com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config"});
        em.setPersistenceUnitName("corePersistenceUnit");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.hibernate.dialect"));
        em.setJpaProperties(properties);

        return em;
    }

    @Bean()
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ClassPathResource("messages.yml"));
        messageSource.setCommonMessages(bean.getObject());
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(0);
        return messageSource;
    }

    @Bean(name = "validator")
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("TrustFort Gateway")
                .version("1.0.0")
                .description("This is TrustFort (API Gateway) created using Spring Boot framework and Java")
                .termsOfService("http://techplaneng.com/terms")
                .license(new License().name("ExpertBridge 1.0").url("http://techplaneng.com/licences")));
    }

    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor dataEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("C*-HL,5He:2.P!L~C"); // encryptor's private key
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);
        return encryptor;
    }

}
