package com.tondo.infrastructure.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private boolean enabled = true;
    private String endpoint = "http://localhost:9090";
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String bucket = "tondo";
}
