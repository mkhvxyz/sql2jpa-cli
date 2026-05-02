package io.github.mkhoi.sql2jpacli.model;

import java.util.Collections;
import java.util.List;

/**
 * Immutable snapshot of a database table parsed from SQL script
 * Design Rules:
 * 1. columns -> Forced immutable (List.copyOf) to block downstream side-effects
 * 2. tableName -> Mandatory, cannot be null or empty
 */

public record TableMetadata(
        String tableName,
        List<ColumnMetadata> columns
) {
    public TableMetadata {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }

        columns = columns == null ? Collections.emptyList() : List.copyOf(columns);
    }
}