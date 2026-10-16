package com.medizano.proveedor.service;

import com.medizano.proveedor.entity.Proveedor;
import com.medizano.proveedor.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    public List<Proveedor> listarActivos() {
        return proveedorRepository.findByActivoTrue();
    }

    public Optional<Proveedor> buscarPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    public Optional<Proveedor> buscarPorRuc(String ruc) {
        return proveedorRepository.findByRuc(ruc);
    }

    @Transactional
    public Proveedor crear(Proveedor proveedor) {
        if (proveedorRepository.findByRuc(proveedor.getRuc()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un proveedor registrado con el RUC: " + proveedor.getRuc());
        }
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    public Proveedor actualizar(Long id, Proveedor datos) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + id));

        existente.setRazonSocial(datos.getRazonSocial());
        existente.setNombreComercial(datos.getNombreComercial());
        existente.setDireccionFiscal(datos.getDireccionFiscal());
        existente.setTelefonoContacto(datos.getTelefonoContacto());
        existente.setEmailContacto(datos.getEmailContacto());
        existente.setRegistroDigemid(datos.getRegistroDigemid());
        if (datos.getActivo() != null) {
            existente.setActivo(datos.getActivo());
        }
        return proveedorRepository.save(existente);
    }

    @Transactional
    public void desactivar(Long id) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + id));
        existente.setActivo(false);
        proveedorRepository.save(existente);
    }
}

