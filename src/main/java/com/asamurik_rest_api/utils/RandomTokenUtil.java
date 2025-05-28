package com.asamurik_rest_api.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomTokenUtil {
    public static String doGenerateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
