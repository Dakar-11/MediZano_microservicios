package com.medizano.proveedor.dto;

import jakarta.validation.Valid;
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
public class RecepcionCompraRequest {

    private String usuarioRecepcion;

    private String observaciones;

    @NotEmpty(message = "Debe especificar los ítems a recibir")
    @Valid
    private List<ItemRecepcionDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemRecepcionDTO {
        @NotNull(message = "El ID del detalle de compra es obligatorio")
        private Long detalleId;

        @NotNull(message = "La cantidad recibida es obligatoria")
        private Integer cantidadRecibida;

        private String batchNumber;

        private LocalDate expiryDate;

        private BigDecimal purchasePrice;
    }
}

