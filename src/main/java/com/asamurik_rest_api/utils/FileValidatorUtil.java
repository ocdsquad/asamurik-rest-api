package com.asamurik_rest_api.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidatorUtil {

    public static boolean isValidType(String contentType, List<String> allowedTypes) {
        return allowedTypes.contains(contentType);
    }

    public static boolean isValidFileSize(long size, long maxSize) {
        return size <= maxSize;
    }

    public static String getFileExtension(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) {
            return "";
        }
        String fileName = file.getOriginalFilename();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static boolean isImageFile(MultipartFile file) {
        return isValidType(file.getContentType(), Arrays.asList("image/jpeg", "image/png", "image/jpg"));
    }
}
