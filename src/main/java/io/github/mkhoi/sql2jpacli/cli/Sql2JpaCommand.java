package io.github.mkhoi.sql2jpacli.cli;

import io.github.mkhoi.sql2jpacli.mapper.JpaMapper;
import io.github.mkhoi.sql2jpacli.model.TableMetadata;
import io.github.mkhoi.sql2jpacli.parser.SqlServerParser;
import io.github.mkhoi.sql2jpacli.utils.NamingUtils;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Root command for the SQL to JPA CLI application.
 * <p>
 * Notes:
 * - Purpose: Defines the command-line interface, expected arguments, and options.
 * - Integration: Managed by Spring context (@Component) to allow dependency injection.
 */
@Component
@Command(
        name = "sql2jpa",
        mixinStandardHelpOptions = true,
        version = "sql2jpa-cli 1.0",
        description = "Converts SQL Server table definitions to Java JPA Entities."
)
public class Sql2JpaCommand implements Callable<Integer> {

    @Option(names = {"-f", "--file"},
            required = true,
            description = "Path to the SQL file containing CREATE TABLE statements.")
    private File sqlFile;

    @Option(names = {"-p", "--package"},
            required = true,
            description = "Target Java package name for the generated entities (e.g., com.example.domain).")
    private String packageName;

    @Option(names = {"-l", "--lombok"},
            description = "Enable Lombok annotations (@Data, @NoArgsConstructor, etc.) instead of standard getters/setters.")
    private boolean useLombok;

    private final SqlServerParser parser;
    private final JpaMapper mapper;

    // Inject SqlServerParser via Spring, manually instantiate JpaMapper
    public Sql2JpaCommand(SqlServerParser parser) {
        this.parser = parser;
        this.mapper = new JpaMapper();
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("===========================================");
        System.out.println("   SQL TO JPA CLI GENERATOR - V1.0");
        System.out.println("===========================================");

        // 1. Check if the SQL file exists and is a valid file
        if (!sqlFile.exists() || !sqlFile.isFile()) {
            System.err.println("❌ Error: SQL file not found at -> " + sqlFile.getAbsolutePath());
            return 1; // Exit code 1: Indicates an error
        }

        try {
            // 2. Read the SQL file content
            String sqlScript = Files.readString(sqlFile.toPath());
            System.out.println("⏳ Reading SQL file: " + sqlFile.getName());

            // 3. Parse the SQL script
            List<TableMetadata> tables = parser.parse(sqlScript);
            if (tables.isEmpty()) {
                System.out.println("⚠️ Warning: No valid CREATE TABLE statements found.");
                return 0; // Exit code 0: Program ran successfully but nothing to do
            }

            // 4. Create the output directory for generated code
            Path outputDir = Paths.get("generated-entities");
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            System.out.println("🚀 Generating " + tables.size() + " entities into: " + outputDir.toAbsolutePath());

            // 5. Generate Java entity files
            for (TableMetadata table : tables) {
                String javaCode = mapper.mapToEntity(table, packageName, useLombok);
                String className = NamingUtils.toPascalCase(table.tableName());

                Path filePath = outputDir.resolve(className + ".java");
                Files.writeString(filePath, javaCode);

                System.out.println("✅ Created: " + className + ".java");
            }

            System.out.println("🎉 Successfully generated all entities!");
            return 0; // Exit code 0: Successful execution

        } catch (Exception e) {
            System.err.println("❌ An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return 1; // Exit code 1: Indicates an error
        }
    }
}