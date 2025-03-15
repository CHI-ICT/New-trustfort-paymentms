/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.payment;

import com.chh.trustfort.payment.tenant.MultiTenantDataSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import java.lang.System.Logger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author Daniel Ofoleta
 */
@Configuration
@ComponentScan
@EntityScan("com.chh.trustfort.admin.model")
@PropertySource("classpath:application.yml")
@EnableTransactionManagement
@EnableWebMvc
@EnableEncryptableProperties
@EnableAsync
@EnableScheduling
public class AppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    Environment env;
    protected Logger logger;

    
   @Bean
    public DataSource multiTenantDataSource() {
        Map<Object, Object> dataSources = new HashMap<>();

        // Configure default tenants (this can be fetched dynamically from a registry)
        dataSources.put(env.getProperty("default-tenant.id"), MultiTenantDataSource.createDataSource(
                "jdbc:postgresql://localhost:5432/trustfort", env.getProperty("default-tenant.db-user"), env.getProperty("default-tenant.db-pass")));
        dataSources.put(env.getProperty("chi-tenant.id"), MultiTenantDataSource.createDataSource(
                "jdbc:postgresql://localhost:5432/chi_db", env.getProperty("chi-tenant.db-user"), env.getProperty("chi-tenant.db-pass")));
        dataSources.put(env.getProperty("hmo-tenant.id"), MultiTenantDataSource.createDataSource(
                "jdbc:postgresql://localhost:5432/hmo_db", env.getProperty("hmo-tenant.db-user"), env.getProperty("hmo-tenant.db-pass")));
        dataSources.put(env.getProperty("cla-tenant.id"), MultiTenantDataSource.createDataSource(
                "jdbc:postgresql://localhost:5432/cla_db", env.getProperty("cla-tenant.db-user"), env.getProperty("cla-tenant.db-pass")));
        dataSources.put(env.getProperty("hfc-tenant.id"), MultiTenantDataSource.createDataSource(
                "jdbc:postgresql://localhost:5432/hfc_db", env.getProperty("hfc-tenant.db-user"), env.getProperty("hfc-tenant.db-pass")));
         dataSources.put(env.getProperty("chh-tenant.id"), MultiTenantDataSource.createDataSource(
                "jdbc:postgresql://localhost:5432/chh_db", env.getProperty("chh-tenant.db-user"), env.getProperty("chh-tenant.db-pass")));

        return new MultiTenantDataSource(dataSources);
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

     @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor getPasswordEncryptor() {
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

    @Bean
    public static Gson createGson() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        JsonSerializer<LocalDate> localDateSerializer = (src, typeOfSrc, context)
                -> new JsonPrimitive(src.format(dateFormatter));

        JsonDeserializer<LocalDate> localDateDeserializer = (json, typeOfT, context)
                -> LocalDate.parse(json.getAsString(), dateFormatter);

        JsonSerializer<LocalDateTime> localDateTimeSerializer = (src, typeOfSrc, context)
                -> new JsonPrimitive(src.format(dateTimeFormatter));

        JsonDeserializer<LocalDateTime> localDateTimeDeserializer = (json, typeOfT, context)
                -> LocalDateTime.parse(json.getAsString(), dateTimeFormatter);

        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, localDateSerializer)
                .registerTypeAdapter(LocalDate.class, localDateDeserializer)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeSerializer)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeDeserializer)
                .create();
    }

}
