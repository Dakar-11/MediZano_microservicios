# S01 - Construcción de un Servicio Base para MediZano POS

## 1. Propósito
Construir el primer microservicio REST base del sistema (**`catalogo-ms`**), configurando la arquitectura por capas, persistencia en PostgreSQL con migraciones versionadas Flyway, documentación interactiva con OpenAPI v3 (Swagger UI) y métricas de salud con Spring Boot Actuator.

## 2. Tecnologías y Dependencias
* **Java 21 / 17** y **Spring Boot 3.x**.
* **Spring Data JPA** y **PostgreSQL Driver**.
* **Flyway Migration** (`classpath:db/migration`).
* **Lombok** y **MapStruct** para mappers DTO $\leftrightarrow$ Entity.
* **SpringDoc OpenAPI WebMvc UI** (`/swagger-ui/index.html`).
* **Spring Boot Actuator** + **Micrometer Prometheus** (`/actuator/prometheus`).

## 3. Endpoints Implementados
* `GET /api/v1/medicamentos`: Lista todos los medicamentos disponibles.
* `GET /api/v1/medicamentos/{id}`: Detalle de medicamento por su identificador.
* `POST /api/v1/medicamentos`: Alta de nuevo producto farmacéutico.
* `PUT /api/v1/medicamentos/{id}`: Actualización de precio y presentación.
* `DELETE /api/v1/medicamentos/{id}`: Borrado lógico.

