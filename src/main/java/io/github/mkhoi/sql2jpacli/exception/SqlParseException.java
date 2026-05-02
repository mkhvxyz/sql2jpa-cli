package io.github.mkhoi.sql2jpacli.exception;

/**
 * Custom exception thrown when the SQL script cannot be parsed.
 * Extends RuntimeException to avoid forcing try-catch everywhere.
 */
public class SqlParseException extends RuntimeException {
    
    public SqlParseException(String message) {
        super(message);
    }
    
}