package com.owo.TP_prg3.Clases.Tienda.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.owo.TP_prg3.Clases.Caja.Caja;
import com.owo.TP_prg3.Clases.Duenio.modelo.Duenio;
import com.owo.TP_prg3.Clases.Duenio.modelo.DuenioRepositorio;
import com.owo.TP_prg3.Clases.Empleado.modelo.Empleado;
import com.owo.TP_prg3.Clases.Empleado.modelo.EmpleadoRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.UsuarioRepositorio;
import com.owo.TP_prg3.Clases.Tienda.dto.FormTiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.dto.TiendaDTO;
import com.owo.TP_prg3.Clases.Tienda.modelo.Tienda;
import com.owo.TP_prg3.Clases.Tienda.modelo.TiendaRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
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
    @Autowired
    private EmpleadoRepositorio empleadoRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Tienda convertir_a_Obj(FormTiendaDTO tiendaDTO) {
        //validarDatosTienda(tiendaDTO);
        //Duenio duenio = validarDuenioExiste(tiendaDTO.getDuenioDni());

        Tienda tienda = new Tienda();
        tienda.setCuit(tiendaDTO.getCuit());
        tienda.setRazonSocial(tiendaDTO.getRazonSocial());
        tienda.setNombreFantasia(tiendaDTO.getNombreFantasia());
        tienda.setCondicion(tiendaDTO.getCondicion());
        tienda.setUrl(tiendaDTO.getUrl()==null? null : tiendaDTO.getUrl());
        tienda.setDireccion(tiendaDTO.getDireccion()); 
        tienda.setIngresosBrutos(tiendaDTO.getIngresosBrutos());
        tienda.setFechaInicioActividades(tiendaDTO.getFechaInicioActividades());
        tienda.setPuntoDeVenta(tiendaDTO.getPuntoDeVenta());    
        tienda.setCaja(new Caja(tiendaDTO.getCaja())); 
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
            tienda.getUrl()==null? null : tienda.getUrl(),
            tienda.getDireccion(),
            tienda.getIngresosBrutos(),
            tienda.getFechaInicioActividades(),
            tienda.getPuntoDeVenta(),
            tienda.getCaja().getSaldo()
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

    // Metodos de caja
    public Optional<LocalDate> obtenerUltimoCierreDeCaja() {
        return tiendaRepositorio.findUltimoCierreCajaById(1L);
    }

    public Boolean cerrarCaja() {
        Tienda tienda = obtenerTiendaPorId(1L);
        tienda.setUltimoCierreCaja(LocalDate.now());
        tiendaRepositorio.save(tienda);
        return true;
    }

    public Map<String, Object> abrirCaja(String email, String password) {
        ValidacionGeneral.validarStringNoVacio(email, "email");
        if (password == null || password.isEmpty()) {
            throw new CampoRequeridoException("contrasenia");
        }

        // Validar credenciales contra un dueño, un empleado o un usuario directo
        Optional<Duenio> duenio = duenioRepositorio.findByUsuario_Email(email.trim());
        Optional<Empleado> empleado = empleadoRepositorio.findByUsuario_Email(email.trim());
        Optional<Usuario> usuario = usuarioRepositorio.findByEmail(email.trim());

        boolean credencialValida = false;
        if (duenio.isPresent()) {
            credencialValida = passwordEncoder.matches(password, duenio.get().getUsuario().getContraseña());
        } else if (empleado.isPresent()) {
            credencialValida = passwordEncoder.matches(password, empleado.get().getUsuario().getContraseña());
        } else if (usuario.isPresent()) {
            credencialValida = passwordEncoder.matches(password, usuario.get().getContraseña());
        }

        if (!credencialValida) {
            return Map.of("exito", false, "mensaje", "Credenciales inválidas.");
        }

        // Abrir la caja del día (limpiar el cierre de hoy para desbloquear el turno)
        Tienda tienda = obtenerTiendaPorId(1L);
        tienda.setUltimoCierreCaja(null);
        tiendaRepositorio.save(tienda);

        return Map.of("exito", true);
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
        tienda.setUrl(updateDTO.getUrl());
        tienda.setDireccion(updateDTO.getDireccion());
        tienda.setPuntoDeVenta(updateDTO.getPuntoDeVenta());
        tienda.getCaja().setSaldo(updateDTO.getCaja());   
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
