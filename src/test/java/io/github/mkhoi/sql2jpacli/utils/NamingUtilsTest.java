package io.github.mkhoi.sql2jpacli.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NamingUtilsTest {

    @Test
    void toCamelCase_ValidSnakeCase_ReturnsCamelCase() {
        assertEquals("roleId", NamingUtils.toCamelCase("role_id"));
        assertEquals("totalAmount", NamingUtils.toCamelCase("TOTAL_AMOUNT"));
        assertEquals("username", NamingUtils.toCamelCase("username"));
        assertEquals("createdAt", NamingUtils.toCamelCase("created_at"));
        assertEquals("mySuperLongVariableName", NamingUtils.toCamelCase("MY_SUPER_LONG_VARIABLE_NAME"));
    }

    @Test
    void toPascalCase_ValidSnakeCase_ReturnsPascalCase() {
        assertEquals("Users", NamingUtils.toPascalCase("users"));
        assertEquals("UserRoles", NamingUtils.toPascalCase("USER_ROLES"));
        assertEquals("OrderDetail", NamingUtils.toPascalCase("order_detail"));
    }

    @Test
    void handleNullOrEmpty_ReturnsOriginalOrEmpty() {
        assertEquals("", NamingUtils.toCamelCase(""));
        assertNull(NamingUtils.toCamelCase(null));
        assertEquals("", NamingUtils.toPascalCase("   "));
    }
}