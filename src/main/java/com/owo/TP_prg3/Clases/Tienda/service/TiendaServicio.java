package com.owo.TP_prg3.Clases.Tienda.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.owo.TP_prg3.Clases.Caja.Caja;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;
import com.owo.TP_prg3.Clases.Duenio.modelo.DuenioRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Tienda.dto.FormTiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.TiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;

@Service
public class TiendaServicio implements I_CRUD<Tienda, TiendaDTO, FormTiendaDTO> {

    private static final String ENTIDAD = "Tienda";

    @Autowired
    private TiendaRepositorio tiendaRepositorio;
    @Autowired
    private DuenioRepositorio duenioRepositorio;

    @Override
    public Tienda convertir_a_Obj(FormTiendaDTO tiendaDTO) {
        //validarDatosTienda(tiendaDTO);
        //Duenio duenio = validarDuenioExiste(tiendaDTO.getDuenioDni());

        Tienda tienda = new Tienda();
        tienda.setCuit(tiendaDTO.getCuit());
        tienda.setRazonSocial(tiendaDTO.getRazonSocial());
        tienda.setNombreFantasia(tiendaDTO.getNombreFantasia());
        tienda.setCondicion(tiendaDTO.getCondicion());
        tienda.setDireccion(tiendaDTO.getDireccion()); 
        tienda.setIngresosBrutos(tiendaDTO.getIngresosBrutos());
        tienda.setFechaInicioActividades(tiendaDTO.getFechaInicioActividades());
        tienda.setPuntoDeVenta(tiendaDTO.getPuntoDeVenta());    
        tienda.setCaja(new Caja(tiendaDTO.getCaja()));
        tienda.setDuenio(duenioRepositorio.findByPersona_Dni(tiendaDTO.getDuenioDni()).get());
        
        return tienda;
    }

    @Override
    public TiendaDTO convertir_a_DTO(Tienda tienda){
        return new TiendaDTO(
            tienda.getTiendaId(),
            tienda.getCuit(),
            tienda.getRazonSocial(),
            tienda.getNombreFantasia(),
            tienda.getCondicion(),
            tienda.getDireccion(),
            tienda.getIngresosBrutos(),
            tienda.getFechaInicioActividades(),
            tienda.getPuntoDeVenta(),
            tienda.getCaja().getSaldo(), 
            tienda.getDuenio().getDni()
        );
    }

    @Override
    public Set<TiendaDTO> obtenerTodos() {
        return tiendaRepositorio.findAll()
                .stream()
                .map(this::convertir_a_DTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<TiendaDTO> buscarPorID(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return tiendaRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    // POST
    @Override
    @Transactional
    public boolean cargar(FormTiendaDTO createDTO) {
        //validarTiendaNoExiste(createDTO.getRazonSocial());
        tiendaRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
    @Override
    public boolean actualizar(Long id, FormTiendaDTO updateDTO) {
        validarDatosTienda(updateDTO);
        Tienda tienda = obtenerTiendaPorId(id);
             
        tienda.setRazonSocial(updateDTO.getRazonSocial().trim());
        tienda.setNombreFantasia(updateDTO.getNombreFantasia().trim());
        tienda.setCondicion(updateDTO.getCondicion());
        tienda.setDireccion(updateDTO.getDireccion());
        tienda.setPuntoDeVenta(updateDTO.getPuntoDeVenta());
        tienda.getCaja().setSaldo(updateDTO.getCaja());
        
        Duenio nuevoDuenio = validarDuenioExiste(updateDTO.getDuenioDni());
        tienda.setDuenio(nuevoDuenio);
        
        tiendaRepositorio.save(tienda);
        return true;
    }


    // DELETE
    @Override
    public boolean eliminar(Long id) {
        Tienda tienda = obtenerTiendaPorId(id);
        tiendaRepositorio.delete(tienda);
        return true;
    }

    @Transactional
    public void acreditarMontoCaja(Long tiendaId, BigDecimal monto) {
        System.out.println("acreditar ------------------------------------------------------------------------------------------------------------------------");
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IngresoInvalidoException("monto", "debe ser un valor positivo.");
        }

        Tienda tienda = obtenerTiendaPorId(tiendaId);

        Caja caja = tienda.getCaja();
        
        // Si la caja es nula, aunque no debería serlo, la re hace
        if (caja == null) { 
            caja = new Caja(BigDecimal.ZERO);
            tienda.setCaja(caja);
        }

        // Sumar el monto al saldo actual
        caja.setSaldo(caja.getSaldo().add(monto));
        tiendaRepositorio.save(tienda);
    }

    @Transactional
    public void debitarMontoCaja(Long tiendaId, BigDecimal monto) {
        System.out.println("Debitar caja ------------------------------------------------------------------------------------------------------------------------");
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IngresoInvalidoException("monto", "debe ser un valor positivo.");
        }

        Tienda tienda = obtenerTiendaPorId(tiendaId);
        Caja caja = tienda.getCaja();
        
        if (caja == null) {
            throw new EntidadNoEncontradaException("La caja de la Tienda no existe para debitar.");
        }
        
        if (caja.getSaldo().compareTo(monto) < 0) {
            throw new IngresoInvalidoException("saldo", "insuficiente para debitar el monto solicitado.");
        }

        caja.setSaldo(caja.getSaldo().subtract(monto)); // Restar monto
        tiendaRepositorio.save(tienda);
    }

    // VALIDACIONES PRIVADAS ===========================================================================================

    private void validarDatosTienda(FormTiendaDTO dto) {
        if (dto == null) {
            throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");
        }
        
        ValidacionGeneral.validarStringNoVacio(dto.getRazonSocial(), "razón social");
        if (dto.getCuit() == null) throw new CampoRequeridoException("CUIT");
        if (dto.getCondicion() == null) throw new CampoRequeridoException("Condición IVA");
        if (dto.getPuntoDeVenta() == null) throw new CampoRequeridoException("Punto de Venta");
        if (dto.getDuenioDni() == null) throw new CampoRequeridoException("DNI Dueño");
        
        ValidacionGeneral.validarDni(dto.getDuenioDni());
    }

    private Duenio validarDuenioExiste(Long dni) {
        return duenioRepositorio.findByPersona_Dni(dni)
                .orElseThrow(() -> new EntidadNoEncontradaException("Duenio", "DNI", dni));
    }

    private void validarTiendaNoExiste(String razonSocial) {
        Optional<Tienda> existente = tiendaRepositorio.findByRazonSocial(razonSocial.trim());
        if (existente.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "razon social", razonSocial.trim());
        }
    }

    private Tienda obtenerTiendaPorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return tiendaRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }

    @Override
    public Set<TiendaDTO> filtrar(String campo, Object valor) {
        throw new UnsupportedOperationException("Unimplemented method 'filtrar'");
    }

    @Override
    public Set<TiendaDTO> ordenar(String campo, boolean ascendente) {
        throw new UnsupportedOperationException("Unimplemented method 'ordenar'");
    }
}
