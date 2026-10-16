# S08 - Mensajería Asíncrona entre Servicios Desacoplados

## 1. Propósito
Desacoplar la creación de pedidos del procesamiento de cobros mediante mensajería basada en eventos (**Event-Driven Architecture**) usando **Apache Kafka**.

## 2. Topics y Flujo de Eventos
1. **Topic `orden-eventos`:**
   * Clave (`Key`): `ordenId`.
   * Evento: `orden.creada` con el monto, cliente y detalles.
2. **Topic `pago-eventos`:**
   * Clave (`Key`): `ordenId`.
   * Eventos: `pago.validado` o `pago.fallido`.

## 3. Monitoreo
Inspección interactiva de particiones, offsets y mensajes en **Kafka UI** (`http://localhost:18085`).

