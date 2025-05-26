package com.example.asamurik_rest_api.utils;

import java.util.Random;

public class OtpGenerator {

    private static final Random random = new Random();

    public static String generateOtp() {
        int otp = random.nextInt(999999);
        return String.valueOf(otp);
    }
}
