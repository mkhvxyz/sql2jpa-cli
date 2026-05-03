package io.github.mkhoi.sql2jpacli.utils;

/**
 * Utility class for mapping SQL data types to Java data types.
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

    /**
     * Maps a SQL data type string to its corresponding Java class name.
     *
     * @param sqlDataType The raw SQL data type (e.g., "VARCHAR(50)", "DECIMAL(18,2)")
     * @return The Java type as a String (e.g., "String", "BigDecimal")
     */
    public static String mapDataType(String sqlDataType) {
        if (sqlDataType == null || sqlDataType.trim().isEmpty()) {
            return "String";
        }

        // Strip precision/scale: "VARCHAR(50)" -> "VARCHAR", "DECIMAL(18,2)" -> "DECIMAL"
        String cleanType = sqlDataType.split("\\(")[0].trim().toUpperCase();

        return switch (cleanType) {
            case "VARCHAR", "NVARCHAR", "TEXT", "CHAR" -> "String";
            case "INT", "INTEGER" -> "Integer";
            case "BIGINT" -> "Long";
            case "DECIMAL", "NUMERIC" -> "BigDecimal";
            case "DATE" -> "LocalDate";
            case "DATETIME", "DATETIME2", "TIMESTAMP" -> "LocalDateTime";
            case "BIT", "BOOLEAN" -> "Boolean";
            case "UNIQUEIDENTIFIER" -> "UUID";
            default -> "String";
        };
    }
}