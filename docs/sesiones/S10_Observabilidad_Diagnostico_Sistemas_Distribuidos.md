# S10 - Observabilidad y Diagnóstico de Sistemas Distribuidos

## 1. Propósito
Monitorear la salud y el rendimiento del sistema mediante recolección centralizada de métricas y logs con correlación distribuida.

## 2. Componentes de la Suite
* **Trazabilidad Distribuida:** Filtro de correlación inyectando `X-Trace-ID` en las cabeceras HTTP y registrando el MDC `traceId` en los logs de Logback.
* **Métricas:** Spring Boot Actuator + Micrometer exportando `/actuator/prometheus`.
* **Servidor Prometheus (:9090):** Scrape periódico de métricas de todos los microservicios.
* **Grafana Loki (:3100) y Alloy:** Recolección y agregación centralizada de logs de contenedores Docker.
* **Grafana Dashboards (:3000):** Visualización de latencia, peticiones por segundo, estados HTTP y búsqueda de logs por `traceId`.

