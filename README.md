# 🚀 SQL to JPA CLI Generator

![Java CI with Maven](https://github.com/MinhKhoixyz/sql2jpa-cli/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

A lightning-fast Command Line Interface (CLI) tool built with **Java 21** and **Spring Boot (Picocli)** that automatically converts raw SQL Server `CREATE TABLE` scripts into clean, production-ready **Java JPA Entities**.

## ✨ Features
- **Regex-based SQL Parsing**: Parses raw T-SQL syntax without requiring a live database connection.
- **Smart Type Mapping**: Automatically maps SQL Server data types (e.g., `DATETIME2`, `UNIQUEIDENTIFIER`) to standard Java/Jakarta types (`LocalDateTime`, `UUID`).
- **Naming Convention Conversion**: Seamlessly converts `snake_case` database identifiers to standard `camelCase` (fields) and `PascalCase` (classes).
- **Lombok Integration**: Supports an optional `--lombok` flag to generate entities with `@Data`, `@NoArgsConstructor`, and `@AllArgsConstructor` instead of boilerplate getters and setters.
- **Test-Driven**: Developed with a TDD approach ensuring 100% core logic coverage.

## 🛠️ Prerequisites
Before running the tool, ensure you have the following installed and configured:
- **Java 21 or higher**: `JAVA_HOME` environment variable must be set, and `java` must be added to your system's `PATH`.
  *Verify by running:* `java -version`
- **Git**: To clone the repository.
- **Maven**: Optional (You can use the provided `./mvnw` wrapper).

## 📥 Installation

Clone the repository to your local machine and navigate into the project directory:
```bash
git clone [https://github.com/MinhKhoixyz/sql2jpa-cli.git](https://github.com/MinhKhoixyz/sql2jpa-cli.git)
cd sql2jpa-cli
```

## 🚀 Usage

### 1. Build the standalone executable
First, compile the source code and build the executable FAT JAR:
```bash
./mvnw clean package -DskipTests
```

### 2. Prepare your SQL File
You can place your `.sql` file **anywhere** on your machine. The tool reads the file via the provided path (absolute or relative). For testing purposes, a sample file is provided at `sql-scripts/sample.sql`.

### 3. Generate Entities
Run the CLI tool using the generated `.jar` file. Here is the standard command:
```bash
java -jar target/sql2jpa-cli-0.0.1-SNAPSHOT.jar -f path/to/your/script.sql -p com.your.package --lombok
```

> **📌 Note:** The generated Java files will be automatically saved in a new folder named `generated-entities/` located in your current working directory.

### ⚙️ CLI Options
| Option | Description | Required |
| :--- | :--- | :---: |
| `-f, --file` | Path to the SQL file containing `CREATE TABLE` statements. | **Yes** |
| `-p, --package`| Target Java package name for the generated entities. | **Yes** |
| `-l, --lombok` | Enable Lombok annotations instead of standard getters/setters. | No |
| `-h, --help` | Show the help message and exit. | No |
| `-V, --version`| Print version information and exit. | No |

---

### 💡 Example

**Input (SQL Script):**
```sql
CREATE TABLE [dbo].[customer_profiles] (
    id INT PRIMARY KEY IDENTITY(1,1),
    full_name NVARCHAR(100) NOT NULL,
    created_at DATETIME2
);
```

**Output (`generated-entities/CustomerProfiles.java`):**
```java
package io.github.mkhoi.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfiles {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

## 🗺️ Roadmap
- [x] Support Microsoft SQL Server (T-SQL) syntax.
- [x] Implement optional Lombok annotation generation.
- [ ] Refactor parser using Strategy Pattern (`DatabaseParser` interface).
- [ ] Add support for **MySQL** dialect.
- [ ] Add support for **PostgreSQL** dialect.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.