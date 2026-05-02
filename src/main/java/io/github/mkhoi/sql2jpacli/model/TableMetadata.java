package io.github.mkhoi.sql2jpacli.model;

import java.util.Collections;
import java.util.List;

/**
 * [EN] Immutable snapshot of a database table parsed from SQL script.
 * [VN] Snapshot cấu trúc bảng. Sử dụng List.copyOf để đảm bảo tính Immutable,
 * chặn đứng side-effect khi data đi qua các phase Mapper/Generator.
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