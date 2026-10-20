# Customer-Service Integration Test Plan

Integration tests exercise the full stack (HTTP → Controller → Application → Domain → Repository) with a real Spring context and in-memory database. No mocks of application or infrastructure layers.

## Scope

- **In scope:** CRUD flows and not-found behavior via REST API (`/clientes`).
- **Out of scope:** RabbitMQ, Docker, cross-service calls.

## Test Scenarios (priority order)

| # | Scenario | Endpoint(s) | Expected behavior |
|---|----------|-------------|-------------------|
| 1 | **Create customer** | `POST /clientes` | 201 Created, `Location` header, response body with `id`, `name`, `identification`, `status` (ACTIVE). Data persisted. |
| 2 | **Get customer** | `POST /clientes` then `GET /clientes/{id}` | Create customer, then GET by id returns 200 with same data. |
| 3 | **Update customer** | `POST /clientes`, `PUT /clientes/{id}` | Create, then PUT with new name/identification; 200 with updated data. |
| 4 | **Delete customer** | `POST /clientes`, `DELETE /clientes/{id}`, `GET /clientes/{id}` | Create, DELETE returns 204; subsequent GET returns 404. |
| 5 | **Not found** | `GET /clientes/{id}`, `PUT /clientes/{id}`, `DELETE /clientes/{id}` | For non-existent id: GET/PUT/DELETE return 404 with consistent error body (`status`, `message`). |

## Design choices

- **Determinism:** Each test uses `@Transactional` and independent data (unique names/identifications) to avoid ordering and pollution.
- **Real behavior:** `@SpringBootTest` + `MockMvc` against real controllers and services; H2 for persistence.
- **Naming:** `shouldCreateCustomerWhen...`, `shouldGetCustomerWhen...`, etc., aligned with TDD skill.
- **API contract:** Request/response DTOs and status codes match existing API (CreateCustomerRequest, UpdateCustomerRequest, CustomerResponse, ErrorResponse).

## Implementation

- Single integration test class: `CustomerApiIntegrationTest`.
- Helper to create a customer via API and return its id for get/update/delete tests.
- No RabbitMQ or external service integration in these tests.
