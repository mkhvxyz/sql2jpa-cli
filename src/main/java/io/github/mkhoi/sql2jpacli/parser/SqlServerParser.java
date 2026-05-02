package io.github.mkhoi.sql2jpacli.parser;

import io.github.mkhoi.sql2jpacli.exception.SqlParseException; // <-- Thêm dòng này
import io.github.mkhoi.sql2jpacli.model.ColumnMetadata;
import io.github.mkhoi.sql2jpacli.model.TableMetadata;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex-based parser for SQL Server scripts.
 * Notes:
 * - Intentional limitation: Ignores table-level constraints. Only supports inline column constraints.
 *
 * - Regex safety: Commas inside parentheses (e.g., DECIMAL(18, 2)) are ignored during string split.
 *
 * - Casing rule: Preserves original casing for table references (e.g., "Roles") to avoid mapping issues,
 *   while definitions are upper-cased for reliable keyword matching.
 */

@Component
public class SqlServerParser {

    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("(?i)CREATE\\s+TABLE\\s+(?:\\[?\\w+\\]?\\.)?\\[?(\\w+)\\]?");
    private static final Pattern FK_PATTERN = Pattern.compile("(?i)REFERENCES\\s+(?:\\[?\\w+\\]?\\.)?\\[?(\\w+)\\]?");
    private static final Pattern DATA_TYPE_PATTERN = Pattern.compile("^\\w+(?:\\s*\\([\\d,\\s]+\\))?");

    public List<TableMetadata> parse(String sqlScript) {
        if (sqlScript == null || sqlScript.trim().isEmpty()) {
            return List.of();
        }

        String cleanScript = removeComments(sqlScript);
        String[] statements = cleanScript.split("(?i);|\\bGO\\b");
        List<TableMetadata> tables = new ArrayList<>();

        for (String statement : statements) {
            String trimmedStatement = statement.trim();
            if (trimmedStatement.toUpperCase().startsWith("CREATE TABLE")) {
                tables.add(parseTableStatement(trimmedStatement));
            }
        }

        return tables;
    }

    private String removeComments(String sql) {
        return sql.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("--.*", "");
    }

    private TableMetadata parseTableStatement(String statement) {
        Matcher nameMatcher = TABLE_NAME_PATTERN.matcher(statement);
        if (!nameMatcher.find()) {
            throw new SqlParseException("Cannot extract table name from statement");
        }
        String tableName = nameMatcher.group(1);

        int firstParen = statement.indexOf('(');
        int lastParen = statement.lastIndexOf(')');
        if (firstParen == -1 || lastParen == -1 || firstParen >= lastParen) {
            throw new SqlParseException("Invalid CREATE TABLE syntax for table: " + tableName);
        }

        String columnsBlock = statement.substring(firstParen + 1, lastParen);
        return new TableMetadata(tableName, parseColumns(columnsBlock));
    }

    private List<ColumnMetadata> parseColumns(String columnsBlock) {
        List<ColumnMetadata> columns = new ArrayList<>();
        String[] lines = columnsBlock.split(",(?![^()]*\\))");

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || isTableLevelConstraint(trimmedLine)) {
                continue;
            }
            columns.add(parseSingleColumn(trimmedLine));
        }
        return columns;
    }

    private boolean isTableLevelConstraint(String line) {
        String upperLine = line.toUpperCase();
        return upperLine.startsWith("PRIMARY KEY") ||
                upperLine.startsWith("FOREIGN KEY") ||
                upperLine.startsWith("CONSTRAINT") ||
                upperLine.startsWith("UNIQUE");
    }

    private ColumnMetadata parseSingleColumn(String columnLine) {
        String normalizedLine = columnLine.replaceAll("\\s+", " ").trim();
        String[] parts = normalizedLine.split(" ", 2);

        if (parts.length < 2) {
            throw new SqlParseException("Invalid column definition: " + columnLine);
        }

        String columnName = parts[0].replaceAll("[\\[\\]\"'`]", "");
        String originalDefinitions = parts[1];
        String upperDefinitions = originalDefinitions.toUpperCase();

        String sqlDataType = extractDataType(upperDefinitions);
        boolean isPrimaryKey = upperDefinitions.contains("PRIMARY KEY");
        boolean isNullable = !upperDefinitions.contains("NOT NULL");
        boolean isUnique = upperDefinitions.contains("UNIQUE");

        String referencedTable = extractReferencedTable(originalDefinitions);

        return new ColumnMetadata(columnName, sqlDataType, isPrimaryKey, isNullable, isUnique, referencedTable);
    }

    private String extractDataType(String definitions) {
        Matcher matcher = DATA_TYPE_PATTERN.matcher(definitions);
        if (matcher.find()) {
            return matcher.group(0).trim();
        }
        return definitions.split(" ")[0];
    }

    private String extractReferencedTable(String definitions) {
        Matcher matcher = FK_PATTERN.matcher(definitions);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}