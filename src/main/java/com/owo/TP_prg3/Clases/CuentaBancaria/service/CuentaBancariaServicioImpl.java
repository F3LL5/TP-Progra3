package com.owo.TP_prg3.Clases.CuentaBancaria.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CreateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.UpdateCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio;
import com.owo.TP_prg3.Excepciones.ConflictoDeDatosException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                        () -> { throw new RecursoNoEncontradoException("Entidad con ID " + cuentaBancariaDTO.getEntidadId() + " no encontrada."); }
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
        return Optional.of(cuentaBancariaRepositorio.findById(id)
                .map(this::convertirA_DTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta bancaria con ID " + id + " no encontrada.")));
    }

    @Override
    public CuentaBancariaDTO createCuentaBancaria(CreateCuentaBancariaDTO createCuentaBancariaDTO) {
        Entidad entidadAsociada = entidadRepositorio.findById(createCuentaBancariaDTO.getEntidadId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe entidad con ID proporcionado."));

        int edad = entidadAsociada.getEdad();
        if (edad <= 17) {
            throw new ConflictoDeDatosException("La entidades menores de edad NO pueden tener cuentas bancarias");
        }

        Optional<CuentaBancariaDTO> existe = getAllCuentasBancarias().stream()
                .filter(cuenta -> cuenta.getEntidadId() == createCuentaBancariaDTO.getEntidadId()).findFirst();
        if (existe.isPresent()) {
            throw new ConflictoDeDatosException("Entidad de dicho ID ya posee una cuenta bancaria.");
        }

        if (createCuentaBancariaDTO.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
            throw new IngresoInvalidoException("El saldo NO puede ser negativo.");
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
                                        () -> { throw new RecursoNoEncontradoException("Entidad con ID " + updateCuentaBancariaDTO.getEntidadId() + " no encontrada."); }
                                );
                    }
                    if (updateCuentaBancariaDTO.getSaldo() != null) {
                        if (updateCuentaBancariaDTO.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
                            throw new IngresoInvalidoException("El saldo no puede ser negativo.");
                        }
                        cuentaBancaria.setSaldo(updateCuentaBancariaDTO.getSaldo());
                    }

                    CuentaBancaria updatedCuentaBancaria = cuentaBancariaRepositorio.save(cuentaBancaria);
                    return convertirA_DTO(updatedCuentaBancaria);
                });
    }

    @Override
    public boolean deleteCuentaBancaria(Long id) {
        if (!cuentaBancariaRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Cuenta bancaria con ID " + id + " no encontrada.");
        }
        cuentaBancariaRepositorio.deleteById(id);
        return true;
    }

    public Optional<CuentaBancariaDTO> getCuentaBancariaByEntidadId(Long entidadId) {
        if (!entidadRepositorio.existsById(entidadId)) {
            throw new RecursoNoEncontradoException("Entidad con ID " + entidadId + " no existe.");
        }

        return cuentaBancariaRepositorio.findAll().stream()
                .filter(cuenta -> cuenta.getEntidad() != null && cuenta.getEntidad().getEntidad_id().equals(entidadId))
                .findFirst()
                .map(this::convertirA_DTO)
                .or(() -> {
                    throw new RecursoNoEncontradoException("No se encontró una cuenta bancaria asociada a la entidad con ID " + entidadId + ".");
                });
    }
}
