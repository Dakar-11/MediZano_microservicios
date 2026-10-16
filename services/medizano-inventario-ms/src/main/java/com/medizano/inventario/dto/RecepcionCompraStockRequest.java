package com.medizano.inventario.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecepcionCompraStockRequest {

    @NotBlank(message = "El número de compra o documento es obligatorio")
    private String numeroCompra;

    private Long proveedorId;

    private String proveedorNombre;

    private String usuarioResponsable;

    @NotEmpty(message = "Debe incluir al menos un ítem recibido")
    @Valid
    private List<ItemRecepcionStock> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemRecepcionStock {
        @NotNull(message = "El ID del medicamento es obligatorio")
        private Long productoId;

        private String productoNombre;

        @NotBlank(message = "El número de lote es obligatorio para trazabilidad FEFO")
        private String batchNumber;

        @NotNull(message = "La fecha de vencimiento es obligatoria")
        private LocalDate expiryDate;

        @NotNull(message = "La cantidad recibida es obligatoria")
        private Integer cantidad;

        private BigDecimal purchasePrice;
    }
}

