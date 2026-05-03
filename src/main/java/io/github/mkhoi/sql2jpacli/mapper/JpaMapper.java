package io.github.mkhoi.sql2jpacli.mapper;

import io.github.mkhoi.sql2jpacli.model.ColumnMetadata;
import io.github.mkhoi.sql2jpacli.model.TableMetadata;
import io.github.mkhoi.sql2jpacli.utils.NamingUtils;
import io.github.mkhoi.sql2jpacli.utils.TypeMapperUtils;

/**
 * Core mapper responsible for generating JPA Entity source code from TableMetadata.
 * <p>
 * Notes:
 * - Purpose: Translates parsed database metadata into standard Java/JPA entity class strings.
 * <p>
 * - Design: Uses StringBuilder for efficient string manipulation. Relies on utility classes
 *   for naming conventions and type mappings. Supports optional Lombok integration or
 *   traditional vanilla Java getter/setter generation.
 * <p>
 * - Output: Generates Jakarta Persistence (jakarta.persistence.*) compatible annotations.
 */
public class JpaMapper {

    public String mapToEntity(TableMetadata table, String packageName, boolean useLombok) {
        if (table == null) {
            throw new IllegalArgumentException("Table metadata cannot be null");
        }

        StringBuilder sb = new StringBuilder();

        // 1. Package Declaration
        if (packageName != null && !packageName.trim().isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }

        // 2. Imports (Using standard Jakarta EE 9+ annotations)
        sb.append("import jakarta.persistence.Entity;\n");
        sb.append("import jakarta.persistence.Table;\n");
        sb.append("import jakarta.persistence.Id;\n");
        sb.append("import jakarta.persistence.Column;\n");

        // Add Lombok imports conditionally
        if (useLombok) {
            sb.append("import lombok.Data;\n");
            sb.append("import lombok.NoArgsConstructor;\n");
            sb.append("import lombok.AllArgsConstructor;\n");
        }
        sb.append("\n");

        // 3. Class Annotations
        sb.append("@Entity\n");
        sb.append("@Table(name = \"").append(table.tableName()).append("\")\n");

        // Add Lombok annotations conditionally
        if (useLombok) {
            sb.append("@Data\n");
            sb.append("@NoArgsConstructor\n");
            sb.append("@AllArgsConstructor\n");
        }

        // 4. Class Definition
        String className = NamingUtils.toPascalCase(table.tableName());
        sb.append("public class ").append(className).append(" {\n\n");

        // 5. Fields Mapping
        for (ColumnMetadata col : table.columns()) {
            // Map Primary Key
            if (col.isPrimaryKey()) {
                sb.append("    @Id\n");
            }

            // Map Column Name
            sb.append("    @Column(name = \"").append(col.columnName()).append("\")\n");

            // Map Data Type and Field Name
            String javaType = TypeMapperUtils.mapDataType(col.sqlDataType());
            String fieldName = NamingUtils.toCamelCase(col.columnName());

            sb.append("    private ").append(javaType).append(" ").append(fieldName).append(";\n\n");
        }

        // 6. Generate Getters and Setters (if Lombok is disabled)
        if (!useLombok) {
            for (ColumnMetadata col : table.columns()) {
                String javaType = TypeMapperUtils.mapDataType(col.sqlDataType());
                String fieldName = NamingUtils.toCamelCase(col.columnName());
                String capitalizedField = NamingUtils.toPascalCase(col.columnName());

                // Getter
                sb.append("    public ").append(javaType).append(" get").append(capitalizedField).append("() {\n");
                sb.append("        return ").append(fieldName).append(";\n");
                sb.append("    }\n\n");

                // Setter
                sb.append("    public void set").append(capitalizedField).append("(").append(javaType).append(" ").append(fieldName).append(") {\n");
                sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
                sb.append("    }\n\n");
            }
        }

        // 7. Close Class
        sb.append("}\n");
        return sb.toString();
    }
}