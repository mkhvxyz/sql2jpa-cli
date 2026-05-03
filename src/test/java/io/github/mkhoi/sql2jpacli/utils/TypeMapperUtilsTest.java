package io.github.mkhoi.sql2jpacli.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeMapperUtilsTest {

    @Test
    void mapDataType_StringTypes_ReturnsString() {
        assertEquals("String", TypeMapperUtils.mapDataType("VARCHAR"));
        assertEquals("String", TypeMapperUtils.mapDataType("NVARCHAR(50)"));
        assertEquals("String", TypeMapperUtils.mapDataType("TEXT"));
        assertEquals("String", TypeMapperUtils.mapDataType("CHAR(10)"));
    }

    @Test
    void mapDataType_IntegerTypes_ReturnsInteger() {
        assertEquals("Integer", TypeMapperUtils.mapDataType("INT"));
        assertEquals("Integer", TypeMapperUtils.mapDataType("INTEGER"));
    }

    @Test
    void mapDataType_LargeNumbers_ReturnsLongOrBigDecimal() {
        assertEquals("Long", TypeMapperUtils.mapDataType("BIGINT"));
        assertEquals("BigDecimal", TypeMapperUtils.mapDataType("DECIMAL(18, 2)"));
        assertEquals("BigDecimal", TypeMapperUtils.mapDataType("DECIMAL"));
        assertEquals("BigDecimal", TypeMapperUtils.mapDataType("NUMERIC"));
    }

    @Test
    void mapDataType_DateTypes_ReturnsTemporal() {
        assertEquals("LocalDate", TypeMapperUtils.mapDataType("DATE"));
        assertEquals("LocalDateTime", TypeMapperUtils.mapDataType("DATETIME"));
        assertEquals("LocalDateTime", TypeMapperUtils.mapDataType("TIMESTAMP"));
    }

    @Test
    void mapDataType_BooleanTypes_ReturnsBoolean() {
        assertEquals("Boolean", TypeMapperUtils.mapDataType("BIT"));
        assertEquals("Boolean", TypeMapperUtils.mapDataType("BOOLEAN"));
    }

    @Test
    void mapDataType_UnknownOrNull_ReturnsStringAsFallback() {
        assertEquals("String", TypeMapperUtils.mapDataType("SOME_WEIRD_TYPE"));
        assertEquals("String", TypeMapperUtils.mapDataType(null));
        assertEquals("String", TypeMapperUtils.mapDataType("   "));
    }
}