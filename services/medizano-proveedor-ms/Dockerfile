FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml ./pom.xml
COPY config-server ./config-server
COPY eureka-server ./eureka-server
COPY api-gateway ./api-gateway
COPY catalogo-ms ./catalogo-ms
COPY cliente-ms ./cliente-ms
COPY orden-ms ./orden-ms
COPY inventario-ms ./inventario-ms
COPY pago-ms ./pago-ms
COPY facturacion-ms ./facturacion-ms
COPY usuario-ms ./usuario-ms
COPY proveedor-ms ./proveedor-ms
RUN mvn clean package -pl proveedor-ms -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /app/proveedor-ms/target/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]

