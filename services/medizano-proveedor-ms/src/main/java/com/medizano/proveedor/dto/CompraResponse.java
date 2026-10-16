package com.medizano.proveedor.dto;

import com.medizano.proveedor.entity.Compra.EstadoCompra;
import com.medizano.proveedor.entity.Compra.TipoDocumento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraResponse {

    private Long id;
    private String numeroDocumento;
    private TipoDocumento tipoDocumento;
    private Long proveedorId;
    private String proveedorRuc;
    private String proveedorRazonSocial;
    private String proveedorTelefono;
    private LocalDate fechaEmision;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaRecepcion;
    private EstadoCompra estado;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal descuento;
    private BigDecimal total;
    private String observaciones;
    private String usuarioRegistro;
    private String usuarioRecepcion;
    private Integer totalItems;
    private List<DetalleCompraResponse> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetalleCompraResponse {
        private Long id;
        private Long productoId;
        private String nombreProducto;
        private Integer cantidadComprada;
        private Integer cantidadRecibida;
        private BigDecimal costoUnitario;
        private BigDecimal descuento;
        private BigDecimal subtotal;
        private String loteSugerido;
        private LocalDate fechaVencimiento;
    }
}

