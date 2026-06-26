package com.tondo.infrastructure.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties({LocalStorageProperties.class, MinioProperties.class})
public class WebStorageConfig implements WebMvcConfigurer {

    private final LocalStorageProperties localStorageProperties;

    public WebStorageConfig(LocalStorageProperties localStorageProperties) {
        this.localStorageProperties = localStorageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(localStorageProperties.getLocalPath()).toAbsolutePath();
        registry.addResourceHandler(localStorageProperties.getPublicUrlPrefix() + "/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
