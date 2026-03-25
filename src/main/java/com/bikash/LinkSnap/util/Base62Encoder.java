package com.bikash.LinkSnap.util;

import java.security.SecureRandom;

public final class Base62Encoder {

    private static final String BASE62 =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final SecureRandom RANDOM = new SecureRandom();

    private Base62Encoder() {
        // prevent instantiation
    }

    public static String encode(long num) {

        if (num == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            int remainder = (int) (num % 62);
            sb.append(BASE62.charAt(remainder));
            num = num / 62;
        }

        return sb.reverse().toString();
    }

    public static String generateRandom(int length) {

        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }

        return code.toString();
    }
}