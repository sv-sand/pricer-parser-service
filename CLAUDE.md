# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Microservice part of the **Pricer** system. Responsible for periodically parsing marketplace prices and storing filtered results in a PostgreSQL database.

## Commands

```bash
# Build
./mvnw clean package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName
```

## Architecture

**Tech stack:** Spring Boot 3.5.7, Java 17, Spring Data JPA, PostgreSQL, Lombok

**Runtime config** (`application.yaml`): Requires env vars `DB_HOST`, `DB_USER`, `DB_PASSWORD`.

### Package structure

```
ru.svsand.pricer.parserservice/
├── Application.java          # Entry point
│
├── logic/                    # Business layer
│   ├── ParserService.java    # Scheduled orchestrator (runs every 60s)
│   ├── parser/
│   │   ├── Parser.java       # Interface + Result/ParsedProduct records
│   │   ├── ParserManager.java # Factory: createParserByStore(), domain conversion
│   │   └── ParserWbApi.java  # Wildberries HTTP client (Java HttpClient + org.json)
│   └── [domain models]       # Product, Search, User, SearchStatistic, Store (enum)
│
└── db/                       # Database layer
    ├── [DAOs]                # JPA entities (ProductDao, SearchDao, etc.)
    ├── [Managers]            # Business logic over repositories (ProductManager, etc.)
    └── [Repositories]        # Spring Data JPA interfaces
```

### Domain vs DAO separation

Domain objects located in the `logic` package are plain Java classes used throughout the logic layer. 
JPA entities located in the `db` package are used for database persistence. Manager classes handle conversion between the two layers.

## Coding Guidelines

- Prioritize clean code, readability, efficiency, and maintainability
- Follow the SOLID and KISS principles
- Follow best practise and design patterns appropriate for the language and framework
- Use early returns when possible
- Always add or modify documentation for public methods and classes when creating new functions and classes or modifying existing ones
- Use AAA pattern and write tests with Arrange, Act, Assert structure

## Coverage requirement

JaCoCo enforces **80% minimum line coverage** per package. CI will fail if this threshold is not met.
