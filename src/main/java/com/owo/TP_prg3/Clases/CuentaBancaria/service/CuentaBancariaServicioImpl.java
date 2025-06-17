package com.owo.TP_prg3.Clases.CuentaBancaria.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CreateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.UpdateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.RolEntidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaBancariaServicioImpl implements CuentaBancariaServicio {

    //Atributos
    @Autowired
    private CuentaBancariaRepositorio cuentaBancariaRepositorio;
    @Autowired
    private EntidadRepositorio entidadRepositorio;

    //Conversion
    private CuentaBancariaDTO convertirA_DTO(CuentaBancaria cuentaBancaria) {
        return new CuentaBancariaDTO(
                cuentaBancaria.getCuentaBancariaId(),
                cuentaBancaria.getEntidad() != null ? cuentaBancaria.getEntidad().getEntidad_id() : null,
                cuentaBancaria.getSaldo()
        );
    }

    private CuentaBancaria convertirA_CuentaBancaria(CreateCuentaBancariaDTO cuentaBancariaDTO) {
        CuentaBancaria cuentaBancaria = new CuentaBancaria();

        entidadRepositorio.findById(cuentaBancariaDTO.getEntidadId())
                .ifPresentOrElse(
                        cuentaBancaria::setEntidad,
                        () -> { throw new EntityNotFoundException("Entidad con ID " + cuentaBancariaDTO.getEntidadId() + " no encontrada."); }
                );

        cuentaBancaria.setSaldo(cuentaBancariaDTO.getSaldo());
        return cuentaBancaria;
    }

    //Metodos
    @Override
    public List<CuentaBancariaDTO> getAllCuentasBancarias() {
        return cuentaBancariaRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<CuentaBancariaDTO> getCuentaBancariaById(Long id) {
        return cuentaBancariaRepositorio.findById(id).map(this::convertirA_DTO);
    }

    @Override
    public String listado(){
        StringBuilder s = new StringBuilder();
        getAllCuentasBancarias().forEach(c -> s
                .append( c.getCuentaBancariaId() + ". ")
                .append( c )
                .append(",\n"));
        return s.toString();
    }

    @Override
    public CuentaBancariaDTO createCuentaBancaria(CreateCuentaBancariaDTO createCuentaBancariaDTO) {
        Entidad entidadAsociada = entidadRepositorio.findById(createCuentaBancariaDTO.getEntidadId())
                .orElseThrow(() -> new RuntimeException("No existe entidad con ID proporcionado."));

        int edad = entidadAsociada.getEdad();
        if (edad < 18) {
            throw new RuntimeException("La entidades menores de edad NO pueden tener cuentas bancarias");
        }

        Optional<CuentaBancariaDTO> existe = getAllCuentasBancarias().stream().filter(cuenta -> cuenta.getEntidadId() == createCuentaBancariaDTO.getEntidadId()).findFirst();
        if (existe.isPresent()) {
            throw new RuntimeException("Entidad de dicho ID ya posee una cuenta bancaria.");
        }

        CuentaBancaria cuentaBancaria = convertirA_CuentaBancaria(createCuentaBancariaDTO);
        CuentaBancaria savedCuentaBancaria = cuentaBancariaRepositorio.save(cuentaBancaria);
        return convertirA_DTO(savedCuentaBancaria);
    }

    @Override
    public Optional<CuentaBancariaDTO> updateCuentaBancaria(Long id, UpdateCuentaBancariaDTO updateCuentaBancariaDTO) {
        return cuentaBancariaRepositorio.findById(id)
                .map(cuentaBancaria -> {
                    if (updateCuentaBancariaDTO.getEntidadId() != null) {
                        entidadRepositorio.findById(updateCuentaBancariaDTO.getEntidadId())
                                .ifPresentOrElse(
                                        cuentaBancaria::setEntidad,
                                        () -> { throw new EntityNotFoundException("Entidad con ID " + updateCuentaBancariaDTO.getEntidadId() + " no encontrada."); }
                                );
                    }
                    if (updateCuentaBancariaDTO.getSaldo() != null) {
                        cuentaBancaria.setSaldo(updateCuentaBancariaDTO.getSaldo());
                    }
                    CuentaBancaria updatedCuentaBancaria = cuentaBancariaRepositorio.save(cuentaBancaria);
                    return convertirA_DTO(updatedCuentaBancaria);
                });
    }

    @Override
    public boolean deleteCuentaBancaria(Long id) {
        if (cuentaBancariaRepositorio.existsById(id)) {
            cuentaBancariaRepositorio.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<CuentaBancariaDTO> getCuentaBancariaByEntidadId(Long entidadId) {
        return cuentaBancariaRepositorio.findAll().stream()
                .filter(cuenta -> cuenta.getEntidad() != null && cuenta.getEntidad().getEntidad_id().equals(entidadId))
                .map(this::convertirA_DTO)
                .findFirst();
    }
}
