package io.github.mkhoi.sql2jpacli.mapper;

import io.github.mkhoi.sql2jpacli.model.ColumnMetadata;
import io.github.mkhoi.sql2jpacli.model.TableMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JpaMapperTest {

    private JpaMapper mapper;
    private TableMetadata mockTable;

    @BeforeEach
    void setUp() {
        mapper = new JpaMapper();
        ColumnMetadata idCol = new ColumnMetadata("id", "INT", true, false, false, null);
        ColumnMetadata nameCol = new ColumnMetadata("user_name", "VARCHAR(50)", false, false, false, null);
        mockTable = new TableMetadata("users", List.of(idCol, nameCol));
    }

    @Test
    void mapToEntity_WithLombok_GeneratesLombokAnnotations() {
        // useLombok = true
        String javaCode = mapper.mapToEntity(mockTable, "io.github.mkhoi.domain", true);

        assertNotNull(javaCode);

        assertTrue(javaCode.contains("package io.github.mkhoi.domain;"));
        assertTrue(javaCode.contains("@Entity"));
        assertTrue(javaCode.contains("public class Users {"));
        assertTrue(javaCode.contains("private Integer id;"));
        assertTrue(javaCode.contains("private String userName;"));

        assertTrue(javaCode.contains("import lombok.Data;"));
        assertTrue(javaCode.contains("@Data"));
        assertTrue(javaCode.contains("@NoArgsConstructor"));

        assertFalse(javaCode.contains("public Integer getId() {"));
        assertFalse(javaCode.contains("public void setUserName(String userName) {"));
    }

    @Test
    void mapToEntity_WithoutLombok_GeneratesGettersAndSetters() {
        // useLombok = false
        String javaCode = mapper.mapToEntity(mockTable, "io.github.mkhoi.domain", false);

        assertNotNull(javaCode);

        assertTrue(javaCode.contains("package io.github.mkhoi.domain;"));
        assertTrue(javaCode.contains("@Entity"));
        assertTrue(javaCode.contains("public class Users {"));

        assertFalse(javaCode.contains("import lombok.Data;"));
        assertFalse(javaCode.contains("@Data"));

        assertTrue(javaCode.contains("public Integer getId() {"));
        assertTrue(javaCode.contains("public void setId(Integer id) {"));
        assertTrue(javaCode.contains("public String getUserName() {"));
        assertTrue(javaCode.contains("public void setUserName(String userName) {"));
    }
}