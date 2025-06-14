package com.owo.TP_prg3.Clases.Transaccion.service;

import com.owo.TP_prg3.Clases.Transaccion.dto.CreateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.TransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.dto.UpdateTransaccionDTO;
import com.owo.TP_prg3.Clases.Transaccion.modelo.Transaccion;
import com.owo.TP_prg3.Clases.Transaccion.modelo.TransaccionRepositorio;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio; // Para inyectar el repositorio de CuentaBancaria
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar para @Transactional
import jakarta.persistence.EntityNotFoundException; // Para manejar casos donde no se encuentra una entidad referenciada

import java.time.LocalDateTime; // Para manejar el campo fecha
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionServicioImpl implements TransaccionServicio {

    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private CuentaBancariaRepositorio cuentaBancariaRepositorio;

    //Conversióm
    private TransaccionDTO convertirA_DTO(Transaccion transaccion) {
        return new TransaccionDTO(
                transaccion.getTransaccionId(),
                transaccion.getTipo(),
                transaccion.getFecha(),
                transaccion.getMonto(),
                transaccion.getCuentaOrigen() != null ? transaccion.getCuentaOrigen().getCuentaBancariaId() : null,
                transaccion.getCuentaDestino() != null ? transaccion.getCuentaDestino().getCuentaBancariaId() : null
        );
    }

    private Transaccion convertirA_Transaccion(CreateTransaccionDTO transaccionDTO) {
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(transaccionDTO.getTipo());
        transaccion.setFecha(LocalDateTime.now()); // La fecha se establece al momento de la creación en el servicio

        // Cargar las entidades de CuentaBancaria si los IDs son proporcionados
        if (transaccionDTO.getCuentaOrigenId() != null) {
            cuentaBancariaRepositorio.findById(transaccionDTO.getCuentaOrigenId())
                    .ifPresentOrElse(
                            transaccion::setCuentaOrigen,
                            () -> { throw new EntityNotFoundException("Cuenta de origen con ID " + transaccionDTO.getCuentaOrigenId() + " no encontrada."); }
                    );
        }

        if (transaccionDTO.getCuentaDestinoId() != null) {
            cuentaBancariaRepositorio.findById(transaccionDTO.getCuentaDestinoId())
                    .ifPresentOrElse(
                            transaccion::setCuentaDestino,
                            () -> { throw new EntityNotFoundException("Cuenta de destino con ID " + transaccionDTO.getCuentaDestinoId() + " no encontrada."); }
                    );
        }

        transaccion.setMonto(transaccionDTO.getMonto());
        return transaccion;
    }

    @Override
    public List<TransaccionDTO> getAllTransacciones() {
        return transaccionRepositorio.findAll()
                .stream()
                .map(this::convertirA_DTO)
                .toList();
    }

    @Override
    public Optional<TransaccionDTO> getTransaccionById(Long id) {
        return transaccionRepositorio.findById(id).map(this::convertirA_DTO);
    }

    @Override
    @Transactional // Aplica la gestión transaccional
    public TransaccionDTO createTransaccion(CreateTransaccionDTO createTransaccionDTO) {
        Transaccion transaccion = convertirA_Transaccion(createTransaccionDTO);
        Transaccion savedTransaccion = transaccionRepositorio.save(transaccion);
        return convertirA_DTO(savedTransaccion);
    }

    @Override
    @Transactional // Aplica la gestión transaccional
    public Optional<TransaccionDTO> updateTransaccion(Long id, UpdateTransaccionDTO updateTransaccionDTO) {
        return transaccionRepositorio.findById(id)
                .map(transaccion -> {
                    if (updateTransaccionDTO.getTipo() != null) {
                        transaccion.setTipo(updateTransaccionDTO.getTipo());
                    }
                    if (updateTransaccionDTO.getFecha() != null) {
                        transaccion.setFecha(updateTransaccionDTO.getFecha());
                    }
                    if (updateTransaccionDTO.getMonto() != null) {
                        transaccion.setMonto(updateTransaccionDTO.getMonto());
                    }

                    if (updateTransaccionDTO.getCuentaOrigenId() != null) {
                        cuentaBancariaRepositorio.findById(updateTransaccionDTO.getCuentaOrigenId())
                                .ifPresentOrElse(
                                        transaccion::setCuentaOrigen,
                                        () -> { throw new EntityNotFoundException("Cuenta de origen con ID " + updateTransaccionDTO.getCuentaOrigenId() + " no encontrada."); }
                                );
                    }

                    if (updateTransaccionDTO.getCuentaDestinoId() != null) {
                        cuentaBancariaRepositorio.findById(updateTransaccionDTO.getCuentaDestinoId())
                                .ifPresentOrElse(
                                        transaccion::setCuentaDestino,
                                        () -> { throw new EntityNotFoundException("Cuenta de destino con ID " + updateTransaccionDTO.getCuentaDestinoId() + " no encontrada."); }
                                );
                    }

                    Transaccion updatedTransaccion = transaccionRepositorio.save(transaccion);
                    return convertirA_DTO(updatedTransaccion);
                });
    }

    @Override
    public boolean deleteTransaccion(Long id) {
        if (transaccionRepositorio.existsById(id)) {
            transaccionRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}
