# Customer-Service API Contract (Person → Customer, PDF-aligned)

Based on the technical challenge PDF:

- **Persona:** nombre, genero, edad, identificación, dirección, teléfono, PK
- **Cliente** (inherits Persona): clienteid, contraseña, estado

API uses English field names; persistence can use Spanish column names if desired.

---

## Endpoints (unchanged)

| Method | Path           | Description        |
|--------|----------------|--------------------|
| POST   | /clientes      | Create customer    |
| GET    | /clientes/{id} | Get by id          |
| PUT    | /clientes/{id} | Update customer    |
| DELETE | /clientes/{id} | Delete customer    |

---

## Request / Response DTOs

### CreateCustomerRequest (POST /clientes)

| Field          | Type   | Required | Validation        | Notes                    |
|----------------|--------|----------|-------------------|--------------------------|
| name           | String | Yes      | @NotBlank         | Person: nombre           |
| gender         | String | No       | -                 | Person: genero           |
| age            | Integer| No       | @Min(0) @Max(150) | Person: edad             |
| identification | String | Yes      | @NotBlank         | Person: identificación   |
| address        | String | No       | -                 | Person: dirección        |
| phone          | String | No       | -                 | Person: teléfono         |
| password       | String | Yes      | @NotBlank         | Cliente: contraseña       |

### UpdateCustomerRequest (PUT /clientes/{id})

| Field          | Type   | Required | Validation        | Notes                          |
|----------------|--------|----------|-------------------|--------------------------------|
| name           | String | Yes      | @NotBlank         | Person: nombre                 |
| gender         | String | No       | -                 | Person: genero                 |
| age            | Integer| No       | @Min(0) @Max(150) | Person: edad                   |
| identification | String | Yes      | @NotBlank         | Person: identificación         |
| address        | String | No       | -                 | Person: dirección              |
| phone          | String | No       | -                 | Person: teléfono               |
| password       | String | No       | -                 | If present, update; else keep  |
| status         | String | No       | -                 | ACTIVE / INACTIVE; default keep|

### CustomerResponse (all success responses)

| Field          | Type   | Notes                    |
|----------------|--------|--------------------------|
| id             | Long   | Cliente: clienteid (PK)  |
| name           | String | Person: nombre           |
| gender         | String | Person: genero (nullable)|
| age            | Integer| Person: edad (nullable)  |
| identification | String | Person: identificación   |
| address        | String | Person: dirección (nullable) |
| phone          | String | Person: teléfono (nullable)  |
| status         | String | Cliente: estado (e.g. ACTIVE, INACTIVE) |

**Note:** `password` is never returned in the response.

---

## Status codes and behavior

- **POST /clientes** → 201 Created, `Location: /clientes/{id}`, body = CustomerResponse; status defaults to ACTIVE.
- **GET /clientes/{id}** → 200 OK, body = CustomerResponse; 404 if not found.
- **PUT /clientes/{id}** → 200 OK, body = CustomerResponse; 404 if not found; 400 on validation errors.
- **DELETE /clientes/{id}** → 204 No Content; 404 if not found.
- Validation errors (e.g. @NotBlank, @Min/@Max) → 400 Bad Request with ErrorResponse.

---

## Domain model (internal)

- **Person** (MappedSuperclass): name, gender, age, identification, address, phone.
- **Customer** (Entity, extends Person): id (PK), password, status. Table holds all columns (single table or joined per JPA strategy).

CRUD behavior for `/clientes` is preserved; all PDF-required fields are supported.
