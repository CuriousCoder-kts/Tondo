package com.tondo.module.file.controller;

import com.tondo.common.response.Result;
import com.tondo.infrastructure.storage.FileStorageService;
import com.tondo.module.user.entity.dto.UpdateProfileDTO;
import com.tondo.module.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final UserService userService;

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestAttribute("userId") Long userId,
                                                     @RequestParam("file") MultipartFile file) {
        String url = fileStorageService.uploadAvatar(userId, file);
        UpdateProfileDTO dto = new UpdateProfileDTO();
        dto.setAvatarUrl(url);
        userService.updateProfile(userId, dto);
        return Result.success(Map.of("url", url));
    }

    @GetMapping("/**")
    public ResponseEntity<Resource> serve(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String prefix = "/api/files/";
        if (!uri.startsWith(prefix) || uri.endsWith("/avatar")) {
            return ResponseEntity.notFound().build();
        }
        String objectPath = uri.substring(prefix.length());
        return fileStorageService.load(objectPath);
    }
}
