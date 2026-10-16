package com.medizano.proveedor.dto;

import com.medizano.proveedor.entity.Compra.TipoDocumento;
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
public class CompraRequest {

    private String numeroDocumento;

    @Builder.Default
    private TipoDocumento tipoDocumento = TipoDocumento.FACTURA;

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    private LocalDate fechaEmision;

    private String observaciones;

    private String usuarioRegistro;

    @Builder.Default
    private BigDecimal igvPorcentaje = new BigDecimal("18.00");

    @NotEmpty(message = "Debe agregar al menos un medicamento a la orden de compra")
    @Valid
    private List<DetalleCompraRequest> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleCompraRequest {
        @NotNull(message = "El ID del medicamento es obligatorio")
        private Long productoId;

        private String nombreProducto;

        @NotNull(message = "La cantidad comprada es obligatoria")
        @com.fasterxml.jackson.annotation.JsonAlias({"cantidadComprada"})
        private Integer cantidad;

        @NotNull(message = "El costo unitario es obligatorio")
        private BigDecimal costoUnitario;

        @Builder.Default
        private BigDecimal descuento = BigDecimal.ZERO;

        private String loteSugerido;

        private LocalDate fechaVencimiento;
    }
}
