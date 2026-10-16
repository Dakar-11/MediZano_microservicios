# S06 - Comunicación Síncrona Resiliente entre Servicios

## 1. Propósito
Implementar tolerancia a fallos en llamadas HTTP entre microservicios utilizando **Spring Cloud OpenFeign** integrado con **Resilience4j** (Circuit Breaker, Timeouts, Retry y Fallback).

## 2. Implementación en MediZano POS
`orden-ms` consume a `catalogo-ms` e `inventario-ms` para validar el precio y reservar existencias antes de emitir la orden.

### Configuración de Resilience4j:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      catalogoClient:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000ms
  timelimiter:
    instances:
      catalogoClient:
        timeout-duration: 2s
  retry:
    instances:
      catalogoClient:
        max-attempts: 3
        wait-duration: 500ms
```

### Clase Fallback:
```java
@Component
public class CatalogoClientFallback implements CatalogoClient {
    @Override
    public MedicineDto obtenerPorId(Long id) {
        throw new ServiceUnavailableException("Servicio de catálogo no disponible temporalmente. Intente en unos momentos.");
    }
}
```

