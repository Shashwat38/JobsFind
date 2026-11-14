
package com.jobsfindai.backend.resume;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDirectory = Path.of("uploads").toAbsolutePath().normalize();

    public String store(MultipartFile file) throws IOException {

        Files.createDirectories(uploadDirectory);

        String storageKey = UUID.randomUUID() + ".pdf";

        Path destination = uploadDirectory.resolve(storageKey);

        Files.copy(file.getInputStream(), destination);

        return storageKey;
    }

    public void delete(String storageKey) throws IOException {
        Files.deleteIfExists(uploadDirectory.resolve(storageKey));
    }

    public Resource load(String storageKey) throws IOException {

        Path filePath = uploadDirectory.resolve(storageKey).normalize();

        if (!filePath.startsWith(uploadDirectory.toAbsolutePath().normalize())) {
            throw new IOException("Invalid storage path");
        }

        return new UrlResource(filePath.toUri());
    }
}
