package com.asamurik_rest_api.utils;

import java.util.Random;

public class OtpGenerator {

    private static final Random random = new Random();

    public static String generateOtp() {

        int otp = random.nextInt(1_000_000);
        return String.format("%06d", otp);

    }
}
