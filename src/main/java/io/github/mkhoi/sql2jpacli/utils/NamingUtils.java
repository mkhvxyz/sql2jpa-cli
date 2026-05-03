package io.github.mkhoi.sql2jpacli.utils;

/**
 * Utility class for converting SQL naming conventions to Java naming conventions.
 * <p>
 * Notes:
 * - Purpose: Transforms database identifiers (e.g., snake_case or SCREAMING_SNAKE_CASE)
 *   into standard Java identifiers (camelCase for fields, PascalCase for classes).
 * <p>
 * - Design: This is a pure stateless utility class. Instantiation is intentionally blocked.
 * <p>
 * - Safety: All methods safely handle null, empty, or blank inputs without throwing exceptions.
 */
public class NamingUtils {

    private NamingUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String toCamelCase(String snakeCase) {
        if (snakeCase == null) {
            return null;
        }
        if (snakeCase.trim().isEmpty()) {
            return "";
        }

        String[] parts = snakeCase.split("_");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            String lowerPart = part.toLowerCase();
            if (sb.isEmpty()) {
                sb.append(lowerPart);
            } else {
                sb.append(Character.toUpperCase(lowerPart.charAt(0)))
                        .append(lowerPart.substring(1));
            }
        }
        return sb.toString();
    }

    public static String toPascalCase(String snakeCase) {
        String camelCase = toCamelCase(snakeCase);

        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
    }
}