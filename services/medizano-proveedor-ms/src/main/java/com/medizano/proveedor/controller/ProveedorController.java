package com.medizano.proveedor.controller;

import com.medizano.proveedor.entity.Proveedor;
import com.medizano.proveedor.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Gestión de laboratorios y proveedores farmacéuticos (No Transaccional)")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @Operation(summary = "Listar todos los proveedores registrados")
    public ResponseEntity<List<Proveedor>> listarTodos(
            @RequestParam(required = false, defaultValue = "false") boolean soloActivos) {
        if (soloActivos) {
            return ResponseEntity.ok(proveedorService.listarActivos());
        }
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar proveedores con búsqueda y paginación")
    public ResponseEntity<Page<Proveedor>> listarPaginado(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") boolean soloActivos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(proveedorService.buscar(search, soloActivos, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Long id) {
        return proveedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/por-ruc/{ruc}")
    @Operation(summary = "Buscar proveedor por RUC")
    public ResponseEntity<Proveedor> obtenerPorRuc(@PathVariable String ruc) {
        return proveedorService.buscarPorRuc(ruc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo laboratorio o proveedor")
    public ResponseEntity<Proveedor> crear(@Valid @RequestBody Proveedor proveedor) {
        Proveedor creado = proveedorService.crear(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del proveedor")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @Valid @RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(proveedorService.actualizar(id, proveedor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar o eliminar proveedor (según reglas de integridad de compras)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proveedorService.eliminarOdesactivar(id);
        return ResponseEntity.noContent().build();
    }
}
