package com.sugamflow.observability.mask;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Central redaction for logs and debug payloads. Never log secrets or clinical content.
 */
public final class SensitiveDataMasker {

    public static final String MASK = "******";

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "passwd", "pwd",
            "token", "accesstoken", "refreshtoken", "idtoken",
            "authorization", "cookie", "set-cookie",
            "otp", "otpcode", "pin",
            "secret", "apikey", "api_key", "apisecret",
            "creditcard", "cardnumber", "cvv", "pan",
            "bankaccount", "accountnumber", "ifsc",
            "jwt", "bearer");

    private static final Pattern JSON_SECRET = Pattern.compile(
            "(?i)(\"(?:password|passwd|pwd|token|accessToken|refreshToken|authorization|otp|secret|apiKey|api_key|cookie)\"\\s*:\\s*\")([^\"]*)(\")");

    private static final Pattern HEADER_SECRET = Pattern.compile(
            "(?i)(authorization|cookie|x-api-key)\\s*[:=]\\s*\\S+(?:\\s+\\S+)*");

    private static final Pattern BEARER = Pattern.compile("(?i)bearer\\s+[A-Za-z0-9\\-._~+/]+=*");

    private SensitiveDataMasker() {}

    public static boolean isSensitiveKey(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        String n = key.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        return SENSITIVE_KEYS.contains(n);
    }

    public static String maskValue(String key, String value) {
        if (value == null) {
            return null;
        }
        return isSensitiveKey(key) ? MASK : value;
    }

    public static String redact(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        String out = BEARER.matcher(text).replaceAll("Bearer " + MASK);
        out = JSON_SECRET.matcher(out).replaceAll("$1" + MASK + "$3");
        return HEADER_SECRET.matcher(out).replaceAll("$1=" + MASK);
    }
}
