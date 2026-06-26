package com.tondo.infrastructure.storage;

import com.tondo.common.exception.BusinessException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif", "application/octet-stream");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    private final ObjectProvider<MinioClient> minioClientProvider;
    private final MinioProperties minioProperties;
    private final LocalStorageProperties localStorageProperties;

    /** 统一走应用内路径，前端通过 /api 代理访问，避免 MinIO 直链无法加载 */
    public String uploadAvatar(Long userId, MultipartFile file) {
        validateFile(file);
        String contentType = resolveContentType(file);
        String ext = extensionFor(contentType, file.getOriginalFilename());
        String objectName = "avatars/" + userId + "/" + UUID.randomUUID() + ext;
        uploadToLocal(file, objectName);
        return publicUrl(objectName);
    }

    public ResponseEntity<Resource> load(String objectPath) {
        Path local = localPath(objectPath);
        if (Files.exists(local)) {
            return buildFileResponse(local);
        }
        if (minioProperties.isEnabled()) {
            MinioClient client = minioClientProvider.getIfAvailable();
            if (client != null) {
                try {
                    InputStream stream = client.getObject(GetObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectPath)
                            .build());
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(guessContentTypeFromPath(objectPath)))
                            .body(new InputStreamResource(stream));
                } catch (Exception ex) {
                    log.warn("MinIO read failed for {}", objectPath);
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    public String normalizePublicUrl(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }
        if (url.startsWith("/api/files/")) {
            return url;
        }
        if (url.startsWith("/uploads/")) {
            return "/api/files/" + url.substring("/uploads/".length());
        }
        String bucketPrefix = minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/";
        if (url.startsWith(bucketPrefix)) {
            return publicUrl(url.substring(bucketPrefix.length()));
        }
        int avatarsIdx = url.indexOf("avatars/");
        if (avatarsIdx >= 0) {
            return publicUrl(url.substring(avatarsIdx));
        }
        return url;
    }

    public String publicUrl(String objectPath) {
        return "/api/files/" + objectPath;
    }

    private Path localPath(String objectPath) {
        return Paths.get(localStorageProperties.getLocalPath(), objectPath).toAbsolutePath().normalize();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择文件");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件不能超过 5MB");
        }
        if (!isAllowedImage(resolveContentType(file), file.getOriginalFilename())) {
            throw new BusinessException("仅支持 JPG/PNG/WebP/GIF 图片");
        }
    }

    private void uploadToLocal(MultipartFile file, String objectName) {
        try {
            Path target = localPath(objectName);
            Files.createDirectories(target.getParent());
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            log.error("Local storage upload failed", ex);
            throw new BusinessException("文件保存失败");
        }
    }

    private ResponseEntity<Resource> buildFileResponse(Path file) {
        try {
            String contentType = Files.probeContentType(file);
            MediaType mediaType = contentType != null
                    ? MediaType.parseMediaType(contentType)
                    : MediaType.APPLICATION_OCTET_STREAM;
            return ResponseEntity.ok().contentType(mediaType).body(new FileSystemResource(file));
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            return contentType.toLowerCase();
        }
        return guessContentType(file.getOriginalFilename());
    }

    private boolean isAllowedImage(String contentType, String filename) {
        if (contentType != null && ALLOWED_TYPES.contains(contentType)) {
            if ("application/octet-stream".equals(contentType)) {
                return isImageFilename(filename);
            }
            return contentType.startsWith("image/");
        }
        return isImageFilename(filename);
    }

    private boolean isImageFilename(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".webp") || lower.endsWith(".gif");
    }

    private String guessContentType(String filename) {
        if (filename == null) return "application/octet-stream";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }

    private String guessContentTypeFromPath(String path) {
        return guessContentType(path);
    }

    private String extensionFor(String contentType, String filename) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> {
                if (filename != null && filename.toLowerCase().endsWith(".png")) yield ".png";
                if (filename != null && filename.toLowerCase().endsWith(".webp")) yield ".webp";
                if (filename != null && filename.toLowerCase().endsWith(".gif")) yield ".gif";
                yield ".jpg";
            }
        };
    }
}
