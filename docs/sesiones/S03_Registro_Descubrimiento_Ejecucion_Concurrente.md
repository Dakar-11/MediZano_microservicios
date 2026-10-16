# S03 - Registro, Descubrimiento y Ejecución Concurrente

## 1. Propósito
Habilitar el descubrimiento dinámico de instancias en tiempo de ejecución mediante **Spring Cloud Netflix Eureka** (`eureka-server` :8761), eliminando IPs o puertos quemados en el código.

## 2. Ejecución Concurrente de Múltiples Instancias
Permite levantar múltiples instancias de un mismo microservicio (por ejemplo `catalogo-ms` en puerto `8081` y otra en `8089` o con puerto dinámico `server.port=0`).

Eureka detecta el heartbeat de cada instancia y mantiene el registro vivo en el dashboard `http://localhost:8761`.

