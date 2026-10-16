package com.medizano.proveedor.controller;

import com.medizano.proveedor.entity.Proveedor;
import com.medizano.proveedor.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<Proveedor>> listarTodos(@RequestParam(required = false, defaultValue = "false") boolean soloActivos) {
        if (soloActivos) {
            return ResponseEntity.ok(proveedorService.listarActivos());
        }
        return ResponseEntity.ok(proveedorService.listarTodos());
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
    @Operation(summary = "Desactivar proveedor (borrado lógico)")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        proveedorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}

