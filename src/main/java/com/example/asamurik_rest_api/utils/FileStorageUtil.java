package com.example.asamurik_rest_api.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileStorageUtil {

    public static String saveFile(MultipartFile file, String directory) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = FileValidatorUtil.getFileExtension(file);
        String newFileName = UUID.randomUUID() + "." + extension;

        Path directoryPath = Path.of(directory);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        Path filePath = directoryPath.resolve(newFileName);
        file.transferTo(filePath.toFile());


        return filePath.toString();
    }
}
