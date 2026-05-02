package io.github.mkhoi.sql2jpacli.parser;

import io.github.mkhoi.sql2jpacli.model.ColumnMetadata;
import io.github.mkhoi.sql2jpacli.model.TableMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlServerParserTest {

    private SqlServerParser parser;

    @BeforeEach
    void setUp() {
        parser = new SqlServerParser();
    }

    @Test
    void parse_ValidSimpleTable_ReturnsTableMetadata() {
        String sql = """
                CREATE TABLE Users (
                    id INT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    role_id INT REFERENCES Roles
                );
                """;

        List<TableMetadata> tables = parser.parse(sql);

        assertEquals(1, tables.size());
        TableMetadata userTable = tables.get(0);
        assertEquals("Users", userTable.tableName());
        assertEquals(3, userTable.columns().size());

        ColumnMetadata idCol = userTable.columns().get(0);
        assertEquals("id", idCol.columnName());
        assertEquals("INT", idCol.sqlDataType());
        assertTrue(idCol.isPrimaryKey());

        ColumnMetadata usernameCol = userTable.columns().get(1);
        assertEquals("username", usernameCol.columnName());
        assertEquals("VARCHAR(50)", usernameCol.sqlDataType());
        assertFalse(usernameCol.isNullable());
        assertTrue(usernameCol.isUnique());

        ColumnMetadata roleIdCol = userTable.columns().get(2);
        assertEquals("role_id", roleIdCol.columnName());
        assertEquals("Roles", roleIdCol.referencedTable());
    }

    @Test
    void parse_TableWithBracketsAndComments_CleansAndParsesCorrectly() {
        String sql = """
                /* This is a comment */
                CREATE TABLE [dbo].[Orders] (
                    [order_id] BIGINT PRIMARY KEY, -- Primary key here
                    [total_amount] DECIMAL(18, 2) NOT NULL
                )
                """;

        List<TableMetadata> tables = parser.parse(sql);

        assertEquals(1, tables.size());
        TableMetadata orderTable = tables.get(0);
        assertEquals("Orders", orderTable.tableName());
        assertEquals(2, orderTable.columns().size());

        ColumnMetadata amountCol = orderTable.columns().get(1);
        assertEquals("total_amount", amountCol.columnName());
        assertEquals("DECIMAL(18, 2)", amountCol.sqlDataType());
    }

    @Test
    void parse_EmptyOrNullScript_ReturnsEmptyList() {
        assertTrue(parser.parse("").isEmpty());
        assertTrue(parser.parse("   ").isEmpty());
        assertTrue(parser.parse(null).isEmpty());
    }

    @Test
    void parse_MultipleTablesSplitByGo_ReturnsMultipleMetadata() {
        String sql = """
                CREATE TABLE TableOne ( id INT );
                GO
                CREATE TABLE TableTwo ( name VARCHAR(10) );
                """;

        List<TableMetadata> tables = parser.parse(sql);
        assertEquals(2, tables.size());
        assertEquals("TableOne", tables.get(0).tableName());
        assertEquals("TableTwo", tables.get(1).tableName());
    }

    @Test
    void parse_InvalidSyntax_ThrowsSqlParseException() {
        String invalidSql = "CREATE TABLE ( id INT );";

        io.github.mkhoi.sql2jpacli.exception.SqlParseException exception = assertThrows(
                io.github.mkhoi.sql2jpacli.exception.SqlParseException.class,
                () -> parser.parse(invalidSql)
        );

        assertTrue(exception.getMessage().contains("Cannot extract table name"));
    }
}