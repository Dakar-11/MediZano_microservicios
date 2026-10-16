package com.medizano.proveedor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 11, max = 11)
    @Column(nullable = false, unique = true, length = 11)
    private String ruc;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String razonSocial;

    @Column(length = 150)
    private String nombreComercial;

    @Column(length = 255)
    private String direccionFiscal;

    @Column(length = 20)
    private String telefonoContacto;

    @Column(length = 150)
    private String emailContacto;

    @Column(length = 50)
    private String registroDigemid;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}

