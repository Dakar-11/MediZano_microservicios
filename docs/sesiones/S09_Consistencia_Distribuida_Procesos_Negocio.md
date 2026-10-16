# S09 - Consistencia Distribuida en Procesos de Negocio

## 1. Propósito
Garantizar la consistencia de datos a través de múltiples bases de datos sin transacciones distribuidas 2PC, aplicando el patrón **Saga Coreografiada**, transacciones de compensación e idempotencia.

## 2. Compensación ante Fallo de Pago
```text
Orden creada (PENDIENTE_PAGO) 
   -> Descuenta stock en inventario 
   -> Kafka publica orden.creada 
   -> Pasarela rechaza tarjeta (TARJETA_RECHAZADA) 
   -> PagoMS publica pago.fallido 
   -> OrdenMS consume evento, cambia estado a CANCELADA 
   -> OrdenMS llama PATCH /restaurar-stock a inventario-ms (Compensación)
```

## 3. Idempotencia en Consumidores
Se implementa una restricción de unicidad (`CONSTRAINT uk_pagos_orden UNIQUE (orden_id)`) respaldada por captura de `DataIntegrityViolationException`, garantizando que mensajes duplicados en Kafka sean ignorados sin duplicar cobros.

