package com.sda.ticketing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path baseDir;

    public FileStorageService(@Value("${storage.local.base-path:uploads}") String basePath) throws IOException {
        this.baseDir = Path.of(basePath).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);
    }

    public String store(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path target = baseDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    public Path resolve(String storagePath) {
        return Path.of(storagePath);
    }
}

