package io.github.mkhoi.sql2jpacli.utils;

/**
 * Utility class for mapping SQL data types to Java data types
 * <p>
 * Notes:
 * - Purpose: Translates database-specific types (e.g., VARCHAR, BIGINT)
 *   into standard Java/JPA types (e.g., String, Long) for entity generation.
 * <p>
 * - Design: This is a pure stateless utility class. Instantiation is intentionally blocked.
 * <p>
 * - Safety: Safely handles null or blank inputs by falling back to "String".
 *   Automatically strips size/precision arguments (e.g., "DECIMAL(18, 2)" becomes "DECIMAL") before matching.
 */
public class TypeMapperUtils {

    private TypeMapperUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String mapDataType(String sqlDataType) {
        if (sqlDataType == null || sqlDataType.trim().isEmpty()) {
            return "String";
        }

        String cleanType = sqlDataType.split("\\(")[0].trim().toUpperCase();

        return switch (cleanType) {
            case "VARCHAR", "NVARCHAR", "TEXT", "CHAR" -> "String";
            case "INT", "INTEGER" -> "Integer";
            case "BIGINT" -> "Long";
            case "DECIMAL", "NUMERIC" -> "BigDecimal";
            case "DATE" -> "LocalDate";
            case "DATETIME", "TIMESTAMP" -> "LocalDateTime";
            case "BIT", "BOOLEAN" -> "Boolean";
            default -> "String";
        };
    }
}