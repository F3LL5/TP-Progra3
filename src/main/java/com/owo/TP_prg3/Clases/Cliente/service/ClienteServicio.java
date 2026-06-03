package com.owo.TP_prg3.Clases.Cliente.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Persona.modelo.Persona;
import com.owo.TP_prg3.Clases.Persona.modelo.PersonaRepositorio;
import com.owo.TP_prg3.Clases.Persona.service.PersonaServicio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.ValidacionGeneral;
import jakarta.transaction.Transactional;
import com.owo.TP_prg3.Clases.Cliente.dto.ClienteDTO;
import com.owo.TP_prg3.Clases.Cliente.modelo.Cliente;
import com.owo.TP_prg3.Clases.Cliente.modelo.ClienteRepositorio;
import com.owo.TP_prg3.Clases.Herramientas.ExcelExportService;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;

@Service
public class ClienteServicio implements I_CRUD<Cliente, ClienteDTO, FormPersonaDTO>{

    private static final String ENTIDAD = "Cliente";

    @Autowired
    private PersonaServicio personaServicio;
    @Autowired
    private PersonaRepositorio personaRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private ExcelExportService excelExportService;

    // Conversión
    @Override
    public Cliente convertir_a_Obj(FormPersonaDTO fDTO) {
        validarDatosCliente(fDTO);

        Cliente cliente = new Cliente();
        cliente.setNombre(fDTO.getNombre());
        cliente.setApellido(fDTO.getApellido());
        cliente.setDni(fDTO.getDni());
        cliente.setFechaNacimiento(fDTO.getFechaNacimiento());

        return cliente;
    }
    
    @Override
    public ClienteDTO convertir_a_DTO(Cliente cliente) {
        return new ClienteDTO(
            cliente.getPersonaId(),
            cliente.getDni(),
            cliente.getNombre(),
            cliente.getApellido(),
            cliente.getFechaNacimiento(),
            cliente.getClienteId()
        );
    }

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------
    
    // GET
    @Override
    public Set<ClienteDTO> obtenerTodos() {
        return clienteRepositorio.findAll().stream() // JPA automáticamente hace el JOIN entre las tablas 'personas' y 'clientes'.
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<ClienteDTO> buscarPorID( Long id) {
        ValidacionGeneral.validarIdValido(id);
        return clienteRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<ClienteDTO> buscarPorDNI(Long dni) {
        return clienteRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    public byte[] exportar() {
        List<ClienteDTO> lista = clienteRepositorio.findAll(Sort.by(Sort.Direction.ASC, "clienteId"))
            .stream()
            .map(this::convertir_a_DTO)
            .collect(Collectors.toList());
        byte[] excel = excelExportService.generarExcel(lista, "Clientes");
        return excel;
    }

    // POST
    @Override
    @Transactional
    public boolean cargar(FormPersonaDTO createDTO) {
        validarDatosCliente(createDTO);
        validarClienteNoExiste(createDTO.getDni());
        // JPA automáticamente realiza el INSERT en la tabla 'personas' y luego en 'clientes'
        clienteRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
   @Transactional
    public boolean actualizar(Long id, FormPersonaDTO updateDTO) {
        validarDatosCliente(updateDTO);
        Cliente cliente = obtenerClientePorId(id);
        
        cliente.setNombre(updateDTO.getNombre());
        cliente.setApellido(updateDTO.getApellido());
        cliente.setDni(updateDTO.getDni());
        cliente.setFechaNacimiento(updateDTO.getFechaNacimiento());

        clienteRepositorio.save(cliente);
        return true;
    }

    // DELETE 
    @Override
    @Transactional
    public boolean eliminar(Long id) {
        Cliente cliente = obtenerClientePorId(id);
        clienteRepositorio.delete(cliente);
        return true;
    }

    

    // VALIDACIONES PRIVADAS ==================================================================================================================================

    /** @param dto El DTO a validar. @throws IngresoInvalidoException si el DTO es nulo. 
        @throws CampoRequeridoException si algún campo obligatorio es nulo o vacío. */
    private void validarDatosCliente(FormPersonaDTO dto) {
        personaServicio.validarDatosPersona(dto);
    }

    /** @param dni El DNI a verificar.
        @throws EntidadDuplicadaException si ya existe un Cliente con ese DNI. */
    private void validarClienteNoExiste(Long dni) {
        // Usamos el método existente 'buscarPorDNI' para chequear
        Optional<ClienteDTO> existente = this.buscarPorDNI(dni);
        Optional<Persona> existentePersona = personaRepositorio.findByDni(dni);

        if (existente.isPresent() || existentePersona.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "dni", dni);
        }
    }

    private Cliente obtenerClientePorId(Long id) {
        ValidacionGeneral.validarIdValido(id);
        return clienteRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }

    @Override
    public Set<ClienteDTO> filtrar(String campo, Object valor) {
        throw new UnsupportedOperationException("Unimplemented method 'filtrar'");
    }

    @Override
    public Set<ClienteDTO> ordenar(String campo, boolean ascendente) {
        throw new UnsupportedOperationException("Unimplemented method 'ordenar'");
    }

}
