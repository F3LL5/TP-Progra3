package com.owo.TP_prg3.Clases.Proveedor.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Proveedor.dto.FormProveedorDTO;
import com.owo.TP_prg3.Clases.Proveedor.dto.ProveedorDTO;
import com.owo.TP_prg3.Clases.Proveedor.modelo.Proveedor;
import com.owo.TP_prg3.Clases.Proveedor.modelo.ProveedorRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;

import jakarta.transaction.Transactional;

import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;

@Service
public class ProveedorServicio implements I_CRUD<Proveedor, ProveedorDTO, FormProveedorDTO> {

    private static final String ENTIDAD = "Proveedor";

    @Autowired
    private ProveedorRepositorio proveedorRepositorio;

    // CONVERSIÓN
    @Override
    public Proveedor convertir_a_Obj(FormProveedorDTO fDTO) {
        validarDatosProveedor(fDTO);
        Proveedor proveedor = new Proveedor();
        
        proveedor.setCuit(fDTO.getCuit());
        proveedor.setRazonSocial(fDTO.getRazonSocial());
        proveedor.setNombreFantasia(fDTO.getNombreFantasia());
        proveedor.setCondicion(fDTO.getCondicion()); 
        proveedor.setDireccion(fDTO.getDireccion()); 
        proveedor.setTelefono(fDTO.getTelefono());
        proveedor.setEmail(fDTO.getEmail());
        
        return proveedor;
    }
    
    @Override
    public ProveedorDTO convertir_a_DTO(Proveedor proveedor) {
        return new ProveedorDTO(
            proveedor.getProveedorId(),
            proveedor.getCuit(),
            proveedor.getRazonSocial(),
            proveedor.getNombreFantasia(),
            proveedor.getCondicion(),
            proveedor.getDireccion(),
            proveedor.getTelefono(),
            proveedor.getEmail());
    }

    // MÉTODOS CRUD -------------------------------------------------------------------------
    
    @Override
    public Set<ProveedorDTO> obtenerTodos() {
        return proveedorRepositorio.findAll().stream()
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<ProveedorDTO> buscarPorID(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return proveedorRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<ProveedorDTO> buscarPorCUIT(Long cuit) {
        return proveedorRepositorio.findByCuit(cuit).map(this::convertir_a_DTO);
    }

    @Override
    @Transactional
    public boolean cargar(FormProveedorDTO createDTO) {
        validarDatosProveedor(createDTO);
        
        // Verificamos si el CUIT ya existe para no duplicar proveedores
        if (proveedorRepositorio.existsByCuit(createDTO.getCuit())) {
            throw new EntidadDuplicadaException(ENTIDAD, "CUIT: " + createDTO.getCuit());
        }

        proveedorRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    @Override
    @Transactional
    public boolean actualizar(Long id, FormProveedorDTO updateDTO) {
        validarDatosProveedor(updateDTO);
        Proveedor proveedor = obtenerProveedorPorId(id);
        
        proveedor.setRazonSocial(updateDTO.getRazonSocial().trim());
        proveedor.setNombreFantasia(updateDTO.getNombreFantasia());
        proveedor.setCondicion(updateDTO.getCondicion());
        proveedor.setDireccion(updateDTO.getDireccion());
        proveedor.setTelefono(updateDTO.getTelefono());
        proveedor.setEmail(updateDTO.getEmail());

        proveedorRepositorio.save(proveedor);
        return true;
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        Proveedor proveedor = obtenerProveedorPorId(id);
        proveedorRepositorio.delete(proveedor);
        return true;
    }

    // VALIDACIONES -------------------------------------------------------------------------

    private void validarDatosProveedor(FormProveedorDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos del proveedor no pueden ser nulos");
        
        ValidacionGeneral.validarStringNoVacio(dto.getCuit().toString(), "CUIT");
        ValidacionGeneral.validarStringNoVacio(dto.getRazonSocial(), "Razón Social");
        
        if (dto.getCondicion() == null) {
            throw new CampoRequeridoException("Condición de IVA");
        }
    }

    private Proveedor obtenerProveedorPorId(Long id) {
        ValidacionGeneral.validarIdValido(id); 
        return proveedorRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }


    //No se van a usar.
    @Override
    public Set<ProveedorDTO> filtrar(String campo, Object valor) {
        throw new UnsupportedOperationException("Unimplemented method 'filtrar'");
    }

    @Override
    public Set<ProveedorDTO> ordenar(String campo, boolean ascendente) {
        throw new UnsupportedOperationException("Unimplemented method 'ordenar'");
    }
}
