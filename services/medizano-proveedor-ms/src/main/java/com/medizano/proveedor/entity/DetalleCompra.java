package com.medizano.proveedor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "detalle_compras", indexes = {
        @Index(name = "idx_detalle_compra_prod", columnList = "productoId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    @JsonIgnore
    private Compra compra;

    @NotNull(message = "El ID del producto es obligatorio")
    @Column(nullable = false)
    private Long productoId;

    @Column(length = 200)
    private String nombreProducto;

    @NotNull(message = "La cantidad comprada es obligatoria")
    @Column(nullable = false)
    private Integer cantidadComprada;

    @Column(nullable = false)
    @Builder.Default
    private Integer cantidadRecibida = 0;

    @NotNull(message = "El costo unitario es obligatorio")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costoUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(length = 50)
    private String loteSugerido;

    @Column
    private LocalDate fechaVencimiento;
}

