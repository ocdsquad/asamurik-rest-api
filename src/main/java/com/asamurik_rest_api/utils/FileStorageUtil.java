package com.asamurik_rest_api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileStorageUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageUtil.class);

    public static String saveFile(MultipartFile file, String directory) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = FileValidatorUtil.getFileExtension(file);
        String newFileName = UUID.randomUUID() + "." + extension;
        logger.debug("Saving file: " + originalFilename + " as " + newFileName + " in directory: " + directory);

        Path directoryPath = Paths.get(directory).toAbsolutePath();
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        logger.debug("testing upload file:" + directoryPath.toString());

        Path filePath = directoryPath.resolve(newFileName);
        logger.debug("testing directory path resolve: " + filePath);

        try {
            file.transferTo(filePath.toFile());
            logger.info("File saved successfully: {}", newFileName);
        } catch (IOException e) {
            logger.error("Error transferring file: {}", e.getMessage(), e);

            throw new IOException("Parent directory does not exist: " + filePath.toFile().getParent());

        }
        return filePath.toString();
    }
}
