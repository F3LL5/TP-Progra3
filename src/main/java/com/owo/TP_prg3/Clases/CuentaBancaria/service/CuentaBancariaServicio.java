package com.owo.TP_prg3.Clases.CuentaBancaria.service;

import com.owo.TP_prg3.Clases.CuentaBancaria.dto.FormCuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.dto.CuentaBancariaDTO;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancaria;
import com.owo.TP_prg3.Clases.CuentaBancaria.modelo.CuentaBancariaRepositorio;
import com.owo.TP_prg3.Clases.Herramientas.ExcelExportService;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CuentaBancariaServicio implements I_CRUD<CuentaBancaria, CuentaBancariaDTO, FormCuentaBancariaDTO> {

    //Atributos
    private static final String ENTIDAD = "CuentaBancaria";

    @Autowired
    private CuentaBancariaRepositorio cbRepositorio;

    @Autowired
    private TiendaRepositorio tiendaRepositorio;

    @Autowired
    private ExcelExportService excelExportService;

    private Tienda getTiendaUnica() {
        // Asumimos que la tienda siempre tiene ID 1.
        return tiendaRepositorio.findById(1L).orElseThrow(()-> new EntidadNoEncontradaException("Por favor, primero realice el registro inicial de la Tienda.") );
    }

    //Conversion
    @Override
    public CuentaBancaria convertir_a_Obj(FormCuentaBancariaDTO fDTO) {
        validarDatosCuenta(fDTO);
        CuentaBancaria cuentaBancaria = new CuentaBancaria();
        cuentaBancaria.setNombreBanco(fDTO.getNombreBanco());
        cuentaBancaria.setSaldo(fDTO.getSaldo());
        cuentaBancaria.setTienda(getTiendaUnica());
        cuentaBancaria.setCbu(fDTO.getCbu());
        
        return cuentaBancaria;
    }

    @Override
    public CuentaBancariaDTO convertir_a_DTO(CuentaBancaria cuentaBancaria) {
        return new CuentaBancariaDTO(
            cuentaBancaria.getCuentaBancariaId(),
            cuentaBancaria.getNombreBanco(),
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
        ValidacionGeneral.validarIdValido(id);
        return cbRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    @Override
    @Transactional
    public boolean cargar(FormCuentaBancariaDTO cDTO) {
        validarDatosCuenta(cDTO);
        validarUnicidadCbu(cDTO.getCbu(), null);
        
        cbRepositorio.save(convertir_a_Obj(cDTO));
        return true;
    }

    @Override
    @Transactional
    public boolean actualizar(Long id, FormCuentaBancariaDTO updateDTO) {
        validarDatosCuenta(updateDTO);
        validarUnicidadCbu(updateDTO.getCbu(), id);
        CuentaBancaria cuenta = obtenerCuentaBancariaPorId(id);
        
        cuenta.setNombreBanco(updateDTO.getNombreBanco());
        cuenta.setCbu(updateDTO.getCbu());
        cuenta.setSaldo(updateDTO.getSaldo());

        cbRepositorio.save(cuenta);
        return true;
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        CuentaBancaria cuenta = obtenerCuentaBancariaPorId(id);

        cbRepositorio.delete(cuenta);
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
        ValidacionGeneral.validarIdValido(cuentaBancariaId);
        ValidacionGeneral.mayorACero(monto, "monto a acreditar");

        CuentaBancaria cuenta = obtenerCuentaBancariaPorId(cuentaBancariaId);
        
        // Sumar el monto al saldo actual
        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cbRepositorio.save(cuenta);
    }

    @Transactional
    public void debitarMonto(Long cuentaBancariaId, BigDecimal monto) {
        ValidacionGeneral.validarIdValido(cuentaBancariaId);
        ValidacionGeneral.mayorACero(monto, "monto a debitar");
        
        CuentaBancaria cuenta = obtenerCuentaBancariaPorId(cuentaBancariaId);
        
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new IngresoInvalidoException("Saldo insuficiente en Cuenta Bancaria para la operación.");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto)); // Restar monto
        cbRepositorio.save(cuenta);
    }

    
    public byte[] exportar() {
        List<CuentaBancariaDTO> lista = cbRepositorio.findAll(Sort.by(Sort.Direction.ASC, "cuentaBancariaId"))
            .stream()
            .map(this::convertir_a_DTO)
            .collect(Collectors.toList());
        byte[] excel = excelExportService.generarExcel(lista, "CuentaBancaria");
        return excel;
    }

    // VALIDACIONES PRIVADAS
    private void validarDatosCuenta(FormCuentaBancariaDTO dto) {
        if (dto == null) throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");

        if (dto.getNombreBanco() == null || dto.getNombreBanco().isBlank()) throw new CampoRequeridoException("nombre del banco");
        if (dto.getCbu() == null) throw new CampoRequeridoException("CBU");
        if (dto.getSaldo() == null) throw new CampoRequeridoException("saldo");

        ValidacionGeneral.mayorACero(dto.getCbu(), "CBU");
        ValidacionGeneral.noNegativo(dto.getSaldo(), "saldo");
    }

    private CuentaBancaria obtenerCuentaBancariaPorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return cbRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }

    private void validarUnicidadCbu(int cbu, Long idExcluido) {
        Optional<CuentaBancaria> duplicado = cbRepositorio.findByCbu(cbu);

        if (duplicado.isPresent()) {
            // Si encontramos un CBU igual, verificamos si pertenece a OTRA cuenta
            if (idExcluido == null || !duplicado.get().getCuentaBancariaId().equals(idExcluido)) {
                throw new EntidadDuplicadaException(
                    ENTIDAD, 
                    "El CBU " + cbu + " ya se encuentra registrado en el sistema."
                );
            }
        }
    }
}
