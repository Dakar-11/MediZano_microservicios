# S04 - Punto Único de Acceso y Distribución de Tráfico

## 1. Propósito
Configurar **Spring Cloud Gateway** (`api-gateway` :8090) como la única puerta de entrada al ecosistema de microservicios, centralizando el enrutamiento dinámico, balanceo de carga (Round-Robin) y políticas de CORS.

## 2. Rutas del Gateway (MediZano POS)
* `/api/v1/auth/**` $\rightarrow$ `lb://usuario-ms`
* `/api/v1/medicamentos/**` $\rightarrow$ `lb://catalogo-ms`
* `/api/v1/clientes/**` $\rightarrow$ `lb://cliente-ms`
* `/api/v1/ordenes/**` $\rightarrow$ `lb://orden-ms`
* `/api/v1/pagos/**` $\rightarrow$ `lb://pago-ms`
* `/api/v1/inventario/**` $\rightarrow$ `lb://inventario-ms`
* `/api/v1/facturas/**` $\rightarrow$ `lb://facturacion-ms`
* `/api/v1/proveedores/**` $\rightarrow$ `lb://proveedor-ms`

