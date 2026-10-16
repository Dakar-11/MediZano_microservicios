# 🎤 Guía Maestra de Exposición y Sustentación del Sistema MediZano POS

Esta guía prepara al equipo de **4 integrantes** para una sustentación técnica de alto impacto ante el jurado y profesor del curso **Desarrollo de Aplicaciones Distribuidas (UPeU 2026-2)**.

---

## 🚀 Tablero de Demostración en Vivo (1-Click)

Durante la exposición, mantén estas pestañas abiertas en el navegador del presentador:

| Servicio / Herramienta | URL Directa Local | Función en la Demostración |
| :--- | :---: | :--- |
| 📖 **Portal de Documentación MkDocs** | [http://localhost:8000/medizano/](http://localhost:8000/medizano/) | Mostrar arquitectura C4, brief técnico y rúbrica oficial S12/S16. |
| 📑 **Swagger UI Centralizado (Gateway)** | [http://localhost:8090/swagger-ui.html](http://localhost:8090/swagger-ui.html) | Ejecución interactiva de endpoints y prueba de Bearer JWT. |
| 🔭 **Eureka Service Discovery** | [http://localhost:8761/](http://localhost:8761/) | Evidenciar las 8 instancias de microservicios registradas dinámicamente. |
| 📨 **Kafka UI (Kafbat)** | [http://localhost:18085/](http://localhost:18085/) | Inspección en tiempo real de los topics `orden-eventos` y `pago-eventos`. |
| 📊 **Grafana Dashboards** | [http://localhost:3000/](http://localhost:3000/) | Paneles de métricas JVM, throughput HTTP y correlación de logs con `traceId` *(admin/admin)*. |
| 📈 **Prometheus Server** | [http://localhost:9090/](http://localhost:9090/) | Consulta de métricas de Spring Boot Actuator (`/actuator/prometheus`). |

---

## ⏱️ Cronograma de Exposición (10 Minutos)

```mermaid
gantt
    title Cronograma de Sustentación en Vivo (10 Minutos)
    dateFormat  mm
    axisFormat  %M:%S
    section Inicio
    Introducción & Arquitectura C4 (Kenyi) :00, 02m
    section Demostración
    Autenticación JWT & RBAC (Igarlos)     :02, 02m
    Catálogo & FEFO Kardex (Aldo)          :04, 02m
    Orden POS & Saga Kafka (Kenyi/Igarlos) :06, 02m
    Facturación SUNAT & DIGEMID (Juan)     :08, 01m
    section Cierre
    Observabilidad & Preguntas (Todos)     :09, 01m
```

---

## 👥 Reparto Individual por Integrante (2 Microservicios c/u)

Cumpliendo con la directiva oficial del sílabo (**1 microservicio transaccional cabecera-detalle + 1 microservicio no transaccional catálogo/maestro por persona**):

### 1. Kenyi Calsin
* **Microservicio Transaccional:** `orden-ms` (`:8082`) — Tablas: `ordenes`, `orden_detalles`.
* **Microservicio No Transaccional:** `cliente-ms` (`:8084`) — Tabla: `clientes` (DNI/RUC, WhatsApp).
* **Defensa Técnica:**
  - Orquestación de ventas POS y comunicación síncrona con `catalogo-ms` e `inventario-ms` mediante **OpenFeign**.
  - Resiliencia con **Resilience4j** (Circuit Breaker con fallback controlado ante caídas).
  - Emisión del evento asíncrono `orden.creada` hacia Apache Kafka.

### 2. Aldo Calla
* **Microservicio Transaccional:** `inventario-ms` (`:8085`) — Tablas: `movimientos_inventario`, `lotes`.
* **Microservicio No Transaccional:** `catalogo-ms` (`:8081`) — Tablas: `medicamentos`, `categorias`.
* **Defensa Técnica:**
  - Control de existencias bajo la regla farmacéutica **FEFO (First Expired, First Out)** para priorizar medicamentos próximos a caducar.
  - Gestión de movimientos Kardex (Entradas, Salidas, Ajustes y Devoluciones).
  - Compensación automática de stock si una orden es cancelada o el pago es rechazado.

### 3. Igarlos Mamani
* **Microservicio Transaccional:** `pago-ms` (`:8083`) — Tabla: `pagos` (`uk_pagos_orden`).
* **Microservicio No Transaccional:** `usuario-ms` (`:8087`) — Tabla: `usuarios` (Roles, Hashes BCrypt).
* **Defensa Técnica:**
  - Seguridad perimetral y distribuida: Emisión y validación de tokens **JWT asimétricos** con roles RBAC (`ROLE_ADMIN`, `ROLE_FARMACEUTICO`, `ROLE_CLIENTE`).
  - Consumidor Kafka idempotente en `pago-ms` para procesar el cobro sin duplicidad ante reintentos de red.
  - Integración con pasarelas de pago externas (PayPal Sandbox y Mercado Pago).

### 4. Juan Chambilla
* **Microservicio Transaccional:** `facturacion-ms` (`:8086`) — Tablas: `facturas`, `factura_items`.
* **Microservicio No Transaccional:** `proveedor-ms` (`:8088`) — Tabla: `proveedores` (Registro Sanitario DIGEMID).
* **Defensa Técnica:**
  - Emisión fiscal electrónica de Boletas y Facturas cumpliendo la estructura SUNAT (cálculo de IGV 18%, montos gravados e inafectos).
  - Consumo del evento `pago.validado` para disparar la facturación de manera 100% desacoplada.
  - Gestión de laboratorios farmacéuticos y contratos de suministro homologados por DIGEMID.

---

## 🔄 Flujo de Demostración End-to-End (Diagrama de Secuencia)

```mermaid
sequenceDiagram
    autonumber
    actor Cajero as Farmacéutico / Cajero
    participant Gateway as API Gateway (:8090)
    participant Auth as usuario-ms (:8087)
    participant Orden as orden-ms (:8082)
    participant Catalogo as catalogo-ms (:8081)
    participant Inventario as inventario-ms (:8085)
    participant Kafka as Apache Kafka (:19092)
    participant Pago as pago-ms (:8083)
    participant Facturacion as facturacion-ms (:8086)

    Cajero->>Gateway: POST /api/usuarios/login (Credenciales)
    Gateway->>Auth: Valida BCrypt y genera JWT
    Auth-->>Cajero: Retorna Token Bearer JWT (ROLE_FARMACEUTICO)

    Cajero->>Gateway: POST /api/ordenes (Crear Venta POS + Token)
    Gateway->>Orden: Enruta solicitud con cabecera Authorization
    Orden->>Catalogo: OpenFeign: Valida precio y receta médica
    Orden->>Inventario: OpenFeign: Reserva temporal de lote (FEFO)
    Orden->>Orden: Persiste cabecera y detalle en PostgreSQL
    Orden->>Kafka: Publica mensaje en topic 'orden-eventos' (orden.creada)
    Orden-->>Cajero: 201 Created (Orden en estado PENDIENTE)

    Kafka->>Pago: Event-Driven: Consume 'orden.creada'
    Pago->>Pago: Procesa cobro (Mercado Pago / PayPal) e inserta con idempotencia
    Pago->>Kafka: Publica mensaje en topic 'pago-eventos' (pago.validado)

    par Consistencia & Notificación
        Kafka->>Orden: Actualiza estado a PAGADO
    and Facturación Electrónica SUNAT
        Kafka->>Facturacion: Consume 'pago.validado' y genera Comprobante Fiscal SUNAT
    end
```

---

## ❓ Banco de Preguntas Típicas del Profesor y Respuestas Clave

### 1. ¿Por qué usamos *Database per Service* y cómo evitamos bloqueos de base de datos?
> **Respuesta:** Cada microservicio es dueño exclusivo de su esquema de PostgreSQL (tenemos 8 bases de datos independientes en el clúster). Ningún microservicio tiene credenciales ni permisos de red para hacer consultas cruzadas o JOINs directos sobre la base de otro servicio. La comunicación se realiza exclusivamente vía contratos HTTP (OpenFeign) para operaciones síncronas o eventos Kafka para operaciones asíncronas.

### 2. ¿Cómo manejan la consistencia distribuida si falla el pago?
> **Respuesta:** Implementamos el patrón **Saga Coreografiada**. Cuando `orden-ms` emite `orden.creada`, los medicamentos quedan reservados. Si `pago-ms` rechaza la transacción (fondos insuficientes o timeout), emite `pago.fallido`. `orden-ms` consume este evento compensatorio, marca la orden como `CANCELADA` y envía una petición compensatoria a `inventario-ms` para devolver el stock a su lote original.

### 3. ¿Cómo garantizan que un mensaje en Kafka no se procese dos veces (Idempotencia)?
> **Respuesta:** En `pago-ms`, la tabla `pagos` cuenta con una restricción de unicidad a nivel de base de datos: `CONSTRAINT uk_pagos_orden UNIQUE (orden_id)`. Además, el consumidor consulta primero si el `orden_id` ya fue procesado antes de invocar a la pasarela de pagos, descartando mensajes duplicados sin efectos colaterales.

### 4. ¿Qué sucede si `catalogo-ms` está caído cuando se intenta crear una orden?
> **Respuesta:** La llamada OpenFeign está envuelta con un **Circuit Breaker de Resilience4j**. Si `catalogo-ms` no responde dentro del umbral configurado (`timeoutDuration: 2s`), se ejecuta el método de fallback que responde con datos en caché o una respuesta controlada de degradación elegante, impidiendo el efecto dominó (cascading failure).

### 5. ¿Cómo se protegen los microservicios y quién valida el JWT?
> **Respuesta:** Aplicamos seguridad en capas (Defense in Depth). El **API Gateway** realiza la primera validación perimetral (formato, expiración y firma criptográfica). Luego, cada microservicio de negocio actúa como un **OAuth2 Resource Server** independiente que extrae los `authorities` del token y aplica control de acceso granular con `@PreAuthorize("hasRole('ROLE_FARMACEUTICO')")`.

---

## 🛠️ Comandos de Demostración en Terminal

```bash
# 1. Comprobar que todos los contenedores están arriba y sanos
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 2. Verificar las 8 bases de datos PostgreSQL creadas
docker exec -it medizano-postgres psql -U medizano_user -d postgres -c "\l"

# 3. Inspeccionar los topics de Kafka directamente en consola
docker exec -it medizano-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# 4. Probar la salud de un microservicio a través del Gateway
curl -s http://localhost:8090/actuator/health | jq .
```

