package com.medizano.proveedor.service;

import com.medizano.proveedor.entity.Proveedor;
import com.medizano.proveedor.repository.CompraRepository;
import com.medizano.proveedor.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private CompraRepository compraRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        proveedor = Proveedor.builder()
                .id(1L)
                .ruc("20100123456")
                .razonSocial("LABORATORIOS PORTUGAL S.R.L.")
                .contactoResponsable("Juan Perez")
                .direccionFiscal("Av. Industrial 123")
                .telefonoContacto("987654321")
                .emailContacto("contacto@portugal.pe")
                .activo(true)
                .build();
    }

    @Test
    void crearProveedor_Valido_Exitoso() {
        when(proveedorRepository.findByRuc("20100123456")).thenReturn(Optional.empty());
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        Proveedor resultado = proveedorService.crear(proveedor);

        assertNotNull(resultado);
        assertEquals("20100123456", resultado.getRuc());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void crearProveedor_RucInvalido_LanzaExcepcion() {
        proveedor.setRuc("12345"); // Invalido (menos de 11 digitos)

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> proveedorService.crear(proveedor));

        assertTrue(ex.getMessage().contains("RUC peruano ingresado es inválido"));
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void crearProveedor_RucDuplicado_LanzaExcepcion() {
        when(proveedorRepository.findByRuc("20100123456")).thenReturn(Optional.of(proveedor));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> proveedorService.crear(proveedor));

        assertTrue(ex.getMessage().contains("Ya existe un proveedor registrado con el RUC"));
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void eliminarOdesactivar_ConCompras_AplicaDesactivacionLogica() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(compraRepository.existsByProveedorId(1L)).thenReturn(true);
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        proveedorService.eliminarOdesactivar(1L);

        assertFalse(proveedor.getActivo());
        verify(proveedorRepository, never()).delete(any(Proveedor.class));
        verify(proveedorRepository, times(1)).save(proveedor);
    }

    @Test
    void eliminarOdesactivar_SinCompras_EliminaFisico() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(compraRepository.existsByProveedorId(1L)).thenReturn(false);

        proveedorService.eliminarOdesactivar(1L);

        verify(proveedorRepository, times(1)).delete(proveedor);
    }
}

