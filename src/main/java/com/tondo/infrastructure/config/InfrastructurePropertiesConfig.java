package com.tondo.infrastructure.config;

import com.tondo.infrastructure.mq.RabbitMqProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RabbitMqProperties.class)
public class InfrastructurePropertiesConfig {
}
