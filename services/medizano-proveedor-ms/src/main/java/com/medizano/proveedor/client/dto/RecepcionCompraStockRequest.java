package com.medizano.proveedor.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecepcionCompraStockRequest {
    private String numeroCompra;
    private Long proveedorId;
    private String proveedorNombre;
    private String usuarioResponsable;
    private List<ItemRecepcionStock> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemRecepcionStock {
        private Long productoId;
        private String productoNombre;
        private String batchNumber;
        private LocalDate expiryDate;
        private Integer cantidad;
        private BigDecimal purchasePrice;
    }
}

