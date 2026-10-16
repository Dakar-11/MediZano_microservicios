package com.medizano.proveedor.service;

import com.medizano.proveedor.client.InventarioClient;
import com.medizano.proveedor.client.dto.RecepcionCompraStockRequest;
import com.medizano.proveedor.dto.CompraRequest;
import com.medizano.proveedor.dto.CompraResponse;
import com.medizano.proveedor.dto.RecepcionCompraRequest;
import com.medizano.proveedor.entity.Compra;
import com.medizano.proveedor.entity.Compra.EstadoCompra;
import com.medizano.proveedor.entity.DetalleCompra;
import com.medizano.proveedor.entity.Proveedor;
import com.medizano.proveedor.repository.CompraRepository;
import com.medizano.proveedor.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private CompraService compraService;

    private Proveedor proveedor;
    private Compra compra;

    @BeforeEach
    void setUp() {
        proveedor = Proveedor.builder()
                .id(1L)
                .ruc("20100123456")
                .razonSocial("LABORATORIOS PORTUGAL S.R.L.")
                .activo(true)
                .build();

        compra = Compra.builder()
                .id(100L)
                .numeroDocumento("COM-20261016-0001")
                .tipoDocumento(Compra.TipoDocumento.FACTURA)
                .proveedor(proveedor)
                .fechaEmision(LocalDate.now())
                .estado(EstadoCompra.PENDIENTE)
                .subtotal(new BigDecimal("100.00"))
                .igv(new BigDecimal("18.00"))
                .total(new BigDecimal("118.00"))
                .detalles(new ArrayList<>())
                .build();

        DetalleCompra det = DetalleCompra.builder()
                .id(200L)
                .compra(compra)
                .productoId(10L)
                .nombreProducto("Paracetamol 500mg")
                .cantidadComprada(50)
                .cantidadRecibida(0)
                .costoUnitario(new BigDecimal("2.00"))
                .subtotal(new BigDecimal("100.00"))
                .loteSugerido("LOTE-P01")
                .fechaVencimiento(LocalDate.now().plusYears(2))
                .build();

        compra.getDetalles().add(det);
    }

    @Test
    void crearCompra_Exitoso() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> {
            Compra c = i.getArgument(0);
            c.setId(100L);
            return c;
        });

        CompraRequest req = CompraRequest.builder()
                .proveedorId(1L)
                .numeroDocumento("F001-000452")
                .items(List.of(
                        CompraRequest.DetalleCompraRequest.builder()
                                .productoId(10L)
                                .nombreProducto("Paracetamol 500mg")
                                .cantidad(50)
                                .costoUnitario(new BigDecimal("2.00"))
                                .build()
                ))
                .build();

        CompraResponse res = compraService.crearCompra(req);

        assertNotNull(res);
        assertEquals("F001-000452", res.getNumeroDocumento());
        assertEquals(new BigDecimal("100.00"), res.getSubtotal());
        assertEquals(new BigDecimal("18.00"), res.getIgv());
        assertEquals(new BigDecimal("118.00"), res.getTotal());
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    void confirmarRecepcion_Exitoso_ComunicaInventario() {
        when(compraRepository.findById(100L)).thenReturn(Optional.of(compra));
        when(compraRepository.save(any(Compra.class))).thenReturn(compra);
        when(inventarioClient.registrarRecepcionCompra(any(RecepcionCompraStockRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        RecepcionCompraRequest req = RecepcionCompraRequest.builder()
                .usuarioRecepcion("admin@medizano.pe")
                .items(List.of(
                        RecepcionCompraRequest.ItemRecepcionDTO.builder()
                                .detalleId(200L)
                                .cantidadRecibida(50)
                                .batchNumber("LOTE-P01-REAL")
                                .expiryDate(LocalDate.now().plusYears(3))
                                .build()
                ))
                .build();

        CompraResponse res = compraService.confirmarRecepcion(100L, req);

        assertNotNull(res);
        assertEquals(EstadoCompra.RECIBIDA, res.getEstado());
        verify(inventarioClient, times(1)).registrarRecepcionCompra(any(RecepcionCompraStockRequest.class));
        verify(compraRepository, times(1)).save(compra);
    }

    @Test
    void cancelarCompra_Pendiente_Exitoso() {
        when(compraRepository.findById(100L)).thenReturn(Optional.of(compra));
        when(compraRepository.save(any(Compra.class))).thenReturn(compra);

        CompraResponse res = compraService.cancelarCompra(100L, "Error de proveedor");

        assertEquals(EstadoCompra.CANCELADA, res.getEstado());
        verify(compraRepository, times(1)).save(compra);
    }

    @Test
    void cancelarCompra_YaRecibida_LanzaExcepcion() {
        compra.setEstado(EstadoCompra.RECIBIDA);
        when(compraRepository.findById(100L)).thenReturn(Optional.of(compra));

        assertThrows(IllegalStateException.class,
                () -> compraService.cancelarCompra(100L, "Motivo"));
        verify(compraRepository, never()).save(any(Compra.class));
    }
}

