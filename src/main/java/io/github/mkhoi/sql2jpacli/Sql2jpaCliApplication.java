package io.github.mkhoi.sql2jpacli;

import io.github.mkhoi.sql2jpacli.cli.Sql2JpaCommand;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class Sql2jpaCliApplication implements CommandLineRunner {

    private final Sql2JpaCommand sql2JpaCommand;
    private final IFactory factory;

    public Sql2jpaCliApplication(Sql2JpaCommand sql2JpaCommand, IFactory factory) {
        this.sql2JpaCommand = sql2JpaCommand;
        this.factory = factory;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(Sql2jpaCliApplication.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        int exitCode = new CommandLine(sql2JpaCommand, factory).execute(args);

        // System.exit(exitCode);
    }
}