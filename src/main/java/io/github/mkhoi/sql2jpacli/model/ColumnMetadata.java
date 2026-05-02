package io.github.mkhoi.sql2jpacli.model;

/**
 * Metadata for individual SQL columns used to trigger JPA annotations.
 * Mapping Rules:
 * 1. referencedTable != null && isUnique == false -> @ManyToOne
 * 2. referencedTable != null && isUnique == true  -> @OneToOne
 * 3. isPrimaryKey == true -> @Id
 */

public record ColumnMetadata(String columnName, String sqlDataType, boolean isPrimaryKey, boolean isNullable,
                             boolean isUnique, String referencedTable) {
    public ColumnMetadata {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        if (sqlDataType == null || sqlDataType.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL data type cannot be null or empty for column: " + columnName);
        }
    }
}