package com.owo.TP_prg3.Clases.CuentaBancaria.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.dto.FormCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;
import com.owo.TP_prg3.Excepciones.StockInsuficienteException;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CuentaBancariaServicio implements I_CRUD<CuentaBancaria, CuentaBancariaDTO, FormCuentaBancariaDTO> {

    //Atributos
    @Autowired
    private CuentaBancariaRepositorio cbRepositorio;

    @Autowired
    private TiendaRepositorio tiendaRepositorio;
    private Tienda getTiendaUnica() {
        // Asumimos que la tienda siempre tiene ID 1.
        return tiendaRepositorio.findById(1L).get();
    }

    //Conversion
    @Override
    public CuentaBancaria convertir_a_Obj(FormCuentaBancariaDTO fDTO) {
        CuentaBancaria cuentaBancaria = new CuentaBancaria();
        cuentaBancaria.setSaldo(fDTO.getSaldo());
        cuentaBancaria.setTienda(getTiendaUnica());
        cuentaBancaria.setCbu(fDTO.getCbu());
        
        return cuentaBancaria;
    }

    @Override
    public CuentaBancariaDTO convertir_a_DTO(CuentaBancaria cuentaBancaria) {
        return new CuentaBancariaDTO(
            cuentaBancaria.getCuentaBancariaId(),
            cuentaBancaria.getCbu(),
            cuentaBancaria.getSaldo()
        );
    }

    //Metodos
    @Override
    public Set<CuentaBancariaDTO> obtenerTodos() {
        return cbRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<CuentaBancariaDTO> buscarPorID(Long id) {
        return cbRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    public boolean cargar(FormCuentaBancariaDTO createDTO) {
        cbRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    @Override
    public boolean actualizar(Long id, FormCuentaBancariaDTO updateDTO) {
        Optional<CuentaBancaria> optional = cbRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        CuentaBancaria cb = optional.get();
        cb.setCbu(updateDTO.getCbu());
        cb.setSaldo(updateDTO.getSaldo());
        if (cb.getTienda()==null) cb.setTienda(getTiendaUnica());

        cbRepositorio.save(cb);
        return true;
    }

    @Override
    public boolean eliminar(Long id) {
        Optional<CuentaBancaria> optional = cbRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else cbRepositorio.delete(optional.get());
        return true;
    }

    @Override
    public Set<CuentaBancariaDTO> filtrar(String campo, Object valor) {
        Stream<CuentaBancaria> stream = cbRepositorio.findAll().stream();

        Predicate<CuentaBancaria> filtro;
        switch (campo.toLowerCase()) {
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<CuentaBancariaDTO> ordenar(String campo, boolean ascendente) {
        Stream<CuentaBancaria> stream = cbRepositorio.findAll().stream();

        Comparator<CuentaBancaria> comparador;
        switch (campo.toLowerCase()) {
            case "cbu"-> comparador = Comparator.comparing(CuentaBancaria::getCbu);
            case "saldo"-> comparador = Comparator.comparing(CuentaBancaria::getSaldo);
            default -> comparador = Comparator.comparing(CuentaBancaria::getCuentaBancariaId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    @Transactional
    public void acreditarMonto(Long cuentaBancariaId, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) return;

        CuentaBancaria cuenta = cbRepositorio.findById(cuentaBancariaId).orElseThrow(() -> new RuntimeException("Cuenta Bancaria no encontrada con ID: " + cuentaBancariaId));
        
        // Sumar el monto al saldo actual
        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cbRepositorio.save(cuenta);
    }

    @Transactional
    public void debitarMonto(Long cuentaBancariaId, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) return; // Validación
        CuentaBancaria cuenta = cbRepositorio.findById(cuentaBancariaId).orElseThrow(() -> new RuntimeException("Cuenta Bancaria no encontrada con ID: " + cuentaBancariaId));
        
        if (cuenta.getSaldo().compareTo(monto) < 0) throw new StockInsuficienteException("Saldo insuficiente en Cuenta Bancaria para la compra."); // Validación Saldo

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto)); // Restar monto
        cbRepositorio.save(cuenta);
    }
}
