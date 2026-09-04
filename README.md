# Customer Onboarding API

Backend REST API that lets customers of "Random Bank" register, log in, and view
their account overview.

---

## Technology

Java 25 · Spring Boot 4 · PostgreSQL 16 · Liquibase · MapStruct · Lombok ·
JUnit 5 · Mockito · Testcontainers · springdoc-openapi · Bucket4j

---

## Running the application

Everything runs from Docker Compose — the application and the PostgreSQL database:

```bash
docker compose up --build
```

The API is then available at `http://localhost:8080`, with interactive documentation at
`http://localhost:8080/swagger-ui.html`.

**Requirements:** Docker and Docker Compose.

### Running the application with Docker
Spins up Postgres and the app together. From the root directory:

```bash
docker compose up --build
```

Then:
Swagger UI at http://localhost:8080/swagger-ui.html
API at http://localhost:8080/api/v1

To stop and discard all data:

```bash
docker compose down -v
```

### Ports

- `8080` The API and Swagger UI
- `9090` Actuator — **not published** by compose, reachable only inside the container network 
- `5432` PostgreSQL, published so the database can be inspected during development 

### Endpoints
- `POST /api/v1/register`   register a customer, opens a current account
- `POST /api/v1/login`      username and password for a JWT
- `GET  /api/v1/overview`   account overview for the logged in customer (requires bearer token)

### Running the tests
Integration tests use Testcontainers, so Docker needs to be running.

```bash
mvn test      # unit + web tests, no Docker needed
mvn verify    # integration tests
```

### Setting up a database connection (not necessary if you run docker compose)
- New connection: PostgreSQL
- Host: localhost
- Port: 5432
- Database: customer_onboarding
- Username: customer_onboarding_app
- Password: localdev

### API documentation
OpenAPI spec is generated from the code. Committed as `openapi.yaml`, also accessible live at
http://localhost:8080/v3/api-docs.yaml

A Postman collection covering the happy path and the error cases is in `postman/`.

### Configuration
Default configuration works out of the box. Overridable via environment variables.


## Design notes

### The 2 requests/second database limit
- Username uniqueness comes from the DB constraint, and it's caught on insert.
- Allowed countries are in the config and not in a lookup table or in an enum.
- Account numbers come from a Postgres sequence with `CACHE 50`, it's unique by construction, there is no collision check
- Entities reference each other by `UUID`, not `@ManyToOne`. There are no lazy loads.
- JWT auth, so authorizing a request costs no database access.

- Currently: Registration = 2 inserts. Login = 1 select. Overview = 1 select.

- Hikari pool is capped at 5, so at most 5 transactions ever hit the DB
at once regardless of traffic.

- A global Bucket4j token bucket returns `429` with `Retry-After` once the
rate is exceeded. It runs first in the filter chain, before auth, so rejected requests are cheap.
Capacity (10) to absorb bursts and refill (2/sec) to enforce a sustained rate.

### Data model
`customer` and `account` are separate tables. Registration creates one account.

Address is `@Embeddable`, never queried on its own, so it flattens into columns inside `customer`.

`account.customer_id` has a real FK, but the Java side has a `UUID` and not a
`@ManyToOne`. Customer and Account are separated.

Overview returns a list even though there's one account. The schema can support more per customer.

### IBAN
`NL` + mod-97 check digits + bank code + 10-digit account number from the sequence
(ISO 13616). Always Dutch, including for Belgian customers, the country code in an IBAN is
the bank's, not the account holder's, and Random Bank is Dutch.

### Allowed countries
Inside config (`app.registration.allowed-countries`). Adding a country requires a restart but it can be added 
via an env var. It is validated at startup (non-empty, two uppercase letters each), so
a typo or a misconfiguration fails the deployment.

### Passwords
Returned in the registration response and stored in plaintext because the assignment says so.
Generator uses `SecureRandom` and skips ambiguous characters (`l`, `I`, `1`, `O`, `0`).

### Auth
JWT/HS256, mainly because verifying it needs no database. Subject is the customer id, which
`/overview` uses directly, the id never comes from a request parameter.

Login returns the same error for unknown username and wrong password. This prevents user enumeration.

There are two filter chains: one matching actuator endpoints, one for everything else. Actuator is also
on an unpublished port, so it's unreachable from outside.

### Layering
```
RegisterCustomerRequest → CustomerDto → Customer
RegisteredCustomerDto   ← Customer
RegisterCustomerResponse ← RegisteredCustomerDto
```

Entities never cross the service boundary.
The services are named after use cases, not tables. Interfaces only exist where a second implementation exists.

### Error handling
`@RestControllerAdvice`. Every error looks the same:

```json
{
  "timestamp": "2026-09-04T10:15:30Z",
  "status": 409,
  "error": "username already taken: JSmith",
  "traceId": "1c3d8a2e-9b0f-4f7e-8d2a-2b1c9e7f4a55"
}
```

Validation failures add a per-field `errors` map. The `traceId` matches the server log.
Stack traces and internal messages never go to the client.

`401`, `403` and `429` are produced before the dispatcher runs, so the security handlers and
the rate limit filter write the same body themselves.

The 409 handler checks the *constraint name* on the Hibernate exception rather than assuming
any integrity violation is a duplicate username.

### Testing
- Unit tests for mappers, services (mocked collaborators), the password generator, the rate
  limiter, and the IBAN generator.
- `@WebMvcTest` per controller, with the real mapper, real security config and real
  exception handler. Assertions against literal JSON so if a field is renamed, it breaks the test.
- `@DataJpaTest` + Testcontainers against a real Postgres built from the changelog.

### Limitations
Out of scope / not implemented:

    Security: plaintext passwords and no refresh tokens or revocation.

    Rate limiting: the bucket is in-memory and per instance, so scaling out multiplies the
    effective limit. It also counts requests, not statements, so the calibration is rough.
    A gateway or a Redis bucket would be better.

    Concurrency: no @Version anywhere.

    DevOps: no CI pipeline, no k8s manifests.