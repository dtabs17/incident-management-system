# Incident Management System
[![Java CI](https://github.com/dtabs17/incident-management-system/actions/workflows/java-ci.yml/badge.svg)](https://github.com/dtabs17/incident-management-system/actions/workflows/java-ci.yml)

A Java 27 command-line application for registering and managing IT service incidents. It demonstrates object-oriented design and modern Java language features through an incident workflow rather than a collection of isolated examples.

## Features

- Register **outages** and **service degradations**, with automatically generated incident IDs.
- Assign engineers and progress incidents through `OPEN → ASSIGNED → IN_PROGRESS → RESOLVED`.
- View incidents, including their severity, status, SLA deadline and impact classification.
- Filter reports by incident status and one or more severity levels.
- Generate immutable report snapshots and export them as UTF-8 text files.
- Handle invalid inputs, invalid state transitions and file-writing failures.

Incident records are held **in memory**. They are reset when the application exits; exporting a report does not save the underlying application state.

## Requirements

- **JDK 27** with preview features enabled at runtime
- **Apache Maven**

The project uses Java 27's preview primitive patterns in `switch` (JEP 532). Maven is configured to enable preview features for compilation and automated tests.

Check that Maven is using JDK 27:

```sh
mvn -version
```

## Build, test and run

From the repository root:

```sh
mvn clean verify
```

This compiles the application, runs the JUnit 5 tests and builds the project package. To launch the console application:

```sh
java --enable-preview -cp target/classes io.github.dtabs17.App
```

**Important:** The `java` command must also resolve to JDK 27. If a different JDK is your system default, call the JDK 27 executable using its full path instead.

The Java 27 preview feature requires `--enable-preview` when running the application outside Maven. Without this flag, the application will not start correctly.

### Example workflow

1. Select **1** to register an outage, entering its title, severity and affected-user count.
2. Select **3** to inspect its ID, status, SLA deadline and impact category.
3. Select **4**, **5** and **6** to assign an engineer, start investigation and resolve it.
4. Select **7** to generate a filtered incident report.
5. Select **8** to export a filtered report to a `.txt` file. For a simple relative path, enter `target/incident-report.txt`.
6. Select **0** to exit.

An incident must be assigned before investigation can start, and must be in progress before it can be resolved.

## Project structure

```text
src/
  main/java/io/github/dtabs17/
    App.java                     Application entry point
    incident/
      cli/                       Interactive terminal interface
      domain/                    Incidents, engineer, enums, SLA and classification
      exception/                 Invalid lifecycle state exception
      report/                    Immutable report and text-file writer
      service/                   Incident manager and reusable filters
  test/java/                     JUnit 5 tests
.github/workflows/java-ci.yml    GitHub Actions build and test workflow
pom.xml                          Maven configuration
```

## Java concepts demonstrated

- **Core OOP:** encapsulation, abstract classes, sealed inheritance, overriding, polymorphism, constructor chaining and overloading.
- **Collections and APIs:** arrays, varargs, `List`/`ArrayList`, `StringBuilder`, `Optional`, streams, `Instant`, `Duration` and `Clock`.
- **Modern Java:** records (`Engineer`), sealed interfaces (`SlaTracked`), switch expressions, type patterns and guarded primitive patterns.
- **Functional programming:** lambdas, `Predicate` composition, method references and effectively final variable capture.
- **Immutability:** immutable `IncidentReport` snapshots with defensive array copying.
- **Error handling:** checked `IOException` for file export and unchecked exceptions for invalid input and lifecycle transitions.

The Java 27 feature is demonstrated in `IncidentImpactClassifier`, where guarded primitive `int` patterns classify outages and degradations by impact. The classification thresholds and SLA windows are example rules defined for this application.

## Tests and CI

JUnit 5 tests cover registration, validation, lifecycle transitions, SLA calculations, filters, report immutability, file export and CLI workflows. Run them with `mvn clean verify`.

The [GitHub Actions workflow](https://github.com/dtabs17/incident-management-system/actions/workflows/java-ci.yml) uses Temurin JDK 27 and runs Maven verification on pull requests to `main` and pushes to `main`.

## Limitations

This is an educational, single-process application. It does not provide a database, user authentication, distributed incident monitoring or persistent incident records. Report exports are snapshots, not backups that can be imported to restore application state.
