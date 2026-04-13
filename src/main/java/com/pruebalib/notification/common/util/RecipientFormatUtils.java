package com.pruebalib.notification.common.util;

public final class RecipientFormatUtils {

    private static final String EMAIL_PATTERN = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    private static final String PHONE_PATTERN = "^\\+?[0-9]{6,15}$";
    private static final String PUSH_TOKEN_PATTERN = "^[A-Za-z0-9:_-]{8,}$";

    private RecipientFormatUtils() {
    }

    public static boolean isEmail(String value) {
        return matches(value, EMAIL_PATTERN);
    }

    public static boolean isPhone(String value) {
        return matches(value, PHONE_PATTERN);
    }

    public static boolean isPushToken(String value) {
        return matches(value, PUSH_TOKEN_PATTERN);
    }

    private static boolean matches(String value, String pattern) {
        return value != null && value.trim().matches(pattern);
    }
}
