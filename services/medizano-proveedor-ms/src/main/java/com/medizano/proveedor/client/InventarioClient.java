package com.medizano.proveedor.client;

import com.medizano.proveedor.client.dto.RecepcionCompraStockRequest;
import com.medizano.proveedor.config.InternalFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventario-ms", configuration = InternalFeignConfig.class)
public interface InventarioClient {

    @PostMapping("/api/v1/inventario/recepcion-compra")
    ResponseEntity<Void> registrarRecepcionCompra(@RequestBody RecepcionCompraStockRequest request);
}

