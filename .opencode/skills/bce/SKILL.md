---
name: bce
description: Architecture and coding rules for CA.micro Quarkus microservices — BCE layering with @Boundary/@Control stereotypes, net.gauntlet package structure, system test modules (-st). Use when creating, scaffolding, generating, writing, or reviewing code in CA.micro projects, Quarkus quickstarters, or projects with net.gauntlet package roots. Triggers on "CA.micro", "microservice", "Quarkus quickstarter", "net.gauntlet", or when working in a project with CA.micro module structure. Extends the microprofile-server skill with specific patterns.
---

## CA.micro Module Structure

- `[subsystem]/` - main application module
- `[subsystem]-st/` - system test module with REST client interfaces and integration tests
- Package: `net.gauntlet.[application].[subsystem].[component].[boundary|control|entity]`
- If only one subsystem exists, `[subsystem]` becomes optional
 

## Business Components (BC)

- Java packages representing key domain concepts — the "home" for related classes
- Named after domain responsibilities — never use "common", "util", "foundation", "base", "core"
- Follow Maximal Cohesion, Minimal Coupling
- A minimalistic CRUD BC hosts 1-10 entities and usually one Business Facade
- Each BC contains three BCE sub-packages: boundary, control, entity
- BCE packages only allowed inside business components, not in root application package
- Not every component needs a boundary package — control can be accessed directly by other components


## BCE Stereotypes & CDI

- use `@Boundary` stereotype on Business Facades (BF) in boundary layer; fall back to `@RequestScoped` if unavailable
- use `@Control` stereotype on Business Activities (BA) in control layer; fall back to `@Dependent` if unavailable
- `@Boundary` and `@Control` stereotypes live in root package `net.gauntlet.[application].[subsystem]`
- never use constructor injection
 

## Boundary Layer (Business Facades)

- thin perimeter between external calls and internal logic — delegate to control, no complex logic
- JAX-RS resources, health checks placed here
- classes not named starting with "Service"
 

## Control Layer (Business Activities)

- product of refactoring complex BFs — encapsulates shared functionality
- procedural, stateless, finer-grained methods than BF
- can be POJO with `@Control`/`@Dependent` or interface with only static methods
- can integrate external systems (e.g., inject MicroProfile REST client)
- includes supporting classes like enums or exceptions
 

## Entity Layer (Business Entities)

- represent key domain concepts with persistent state and behavior
- can be implemented as Java records with `toJSON()` returning `JsonObject` and `fromJSON(JsonObject)` static factory
- DTOs only when structure significantly differs from entities — otherwise use entities directly
- model value objects as enums
 

## Copyright Header

All files include:

```java

//==============================================================================

// Copyright (c) 2026 net.gauntlet. All rights reserved.

//==============================================================================

```

 

## Dependencies

- Quarkus 3.27.0 on Java 21, MicroProfile (metrics, health, config, fault-tolerance, OpenAPI)
- prefer `quarkus-smallrye-*` dependencies for MicroProfile and Jakarta EE APIs
- prefer Java SE > MicroProfile > Jakarta EE over external libraries
- AssertJ for assertions, Maven Failsafe for integration/system tests
- edit `pom.xml` only with explicit approval
- never use quarkus-hibernate-validator
 

## JAX-RS

- resources named in plural: `GreetingsResource` not `GreetingResource`
- `@Consumes` and `@Produces` on class-level
- prefer returning `Response` over `JsonObject`
- delegate to control layer — no business logic in resources
- inherit from `WebApplicationException` for proper status codes; use `BadRequestException` for BAD_REQUEST
- use `@Metered(absolute = true)` on endpoints for torture test metrics 
- prefer the Quarkus dependency `quarkus-rest-jackson` for the rest implementation
 

## JSON Processing (JSON-P)

- prefer JSON-P over JSON-B for semi-structured data and API evolution
- use `JsonPointer` (RFC 6901) to extract data from large JSON structures
- map JSON-P to entities in boundary layer before passing to control
 

## Development Workflow

```bash
cd [subsystem] && mvn compile quarkus:dev          # dev mode at localhost:8080
cd [subsystem] && mvn package                       # build
java -jar target/quarkus-app/quarkus-run.jar        # run
```

## Testing

- unit tests: `*Test.java` — Surefire
- integration tests: `*IT.java` in main module — `@QuarkusTest`, Failsafe
- system tests: `*IT.java` in `-st` module — REST client against running service
- test method names do not start with "test" or "should"
- use AssertJ assertions, not JUnit
- skip tests for trivial code (getters, records, enums)
- at most 3 tests per feature: happy path, error case, edge case
- no private visibility in tests
 

## System Test Module (`-st`)

- REST client interfaces in `src/main/java` (reusable)
- all interfaces MUST end with `Client` suffix: `GreetingsResourceClient`, not `GreetingsResource`
- file path: `[subsystem]-st/src/main/java/com/bmw/[application]/[subsystem]/[component]/boundary/[ResourceName]Client.java`
- use `@RegisterRestClient(configKey = "service_uri")` — reuse existing configKey, never create new ones
- configure in `application.properties`: `service_uri/mp-rest/url=http://localhost:8080`
- test classes in `src/test/java` ending with `IT`
- REST client methods must return `Response`

```bash
cd [subsystem]-st && mvn compile failsafe:integration-test                          # system tests
cd [subsystem]-st && mvn compile -Dverify.results=true failsafe:integration-test    # with stress tests

```
 

## Java Style

- use Java 21 features: `var`, pattern matching, text blocks, records
- Logger: `static final System.Logger LOG = System.getLogger(ClassName.class.getName());`
- prefer `List.of()`, `Stream` API, `toList()` over loops; avoid `forEach`
- prefer `Stream.of` over `Arrays.stream`
- no `final` on fields or method parameters
- package-private visibility by default; avoid private unless necessary
- method references over multiline lambdas — extract to well-named helper method if braces needed
- import statements over fully qualified names
- use `this` to reference instance fields
- prefer try-with-resources over explicit closing
 

## Class Naming

- name after responsibilities, not patterns — avoid `*Impl`, `*Service`, `*Manager`, `*Factory`
- must not end with `*Control`, `*Creator` (use plural instead)
- `*Builder` only for method chaining, `*Stack` only for infrastructure stacks
- interfaces only for multiple implementations (strategy pattern)
- records for immutable data with factory methods instead of null parameters
- no getters starting with "get" — use record convention: `message()` not `getMessage()`
 

## Documentation

- `package-info.java` required for business components — document domain responsibilities, not BCE pattern
- skip `package-info.java` for trivial components with obvious meaning
- JavaDoc explains "why", never rephrases code
- keep READMEs brief and developer-focused
 

## Architecture Boundaries

- use virtual threads for blocking I/O, not reactive APIs
- `@Transactional` only in boundary layer (if unavoidable elsewhere, document why in JavaDoc)
- `quarkus.swagger-ui.always-include=true` in production

