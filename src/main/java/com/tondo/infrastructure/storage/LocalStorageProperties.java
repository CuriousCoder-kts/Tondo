package com.tondo.infrastructure.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tondo.storage")
public class LocalStorageProperties {
    /** 本地存储目录（相对项目运行目录） */
    private String localPath = "uploads";
    /** 对外访问路径前缀 */
    private String publicUrlPrefix = "/uploads";
}
