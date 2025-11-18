package com.owo.TP_prg3.Clases.Proveedor.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Persona.dto.FormPersonaDTO;
import com.owo.TP_prg3.Clases.Proveedor.dto.ProveedorDTO;
import com.owo.TP_prg3.Clases.Proveedor.modelo.Proveedor;
import com.owo.TP_prg3.Clases.Proveedor.modelo.ProveedorRepositorio;
import com.owo.TP_prg3.Excepciones.CampoRequeridoException;
import com.owo.TP_prg3.Excepciones.EntidadDuplicadaException;
import com.owo.TP_prg3.Excepciones.EntidadNoEncontradaException;
import com.owo.TP_prg3.Excepciones.IngresoInvalidoException;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;

@Service
public class ProveedorServicio implements I_CRUD<Proveedor, ProveedorDTO, FormPersonaDTO>{

    private static final String ENTIDAD = "Proveedor";

    @Autowired
    private ProveedorRepositorio proveedorRepositorio;

    // Conversión
    @Override
    public Proveedor convertir_a_Obj(FormPersonaDTO fDTO) {
        validarDatosProveedor(fDTO);
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(fDTO.getNombre());
        proveedor.setEdad(fDTO.getEdad());
        proveedor.setDni(fDTO.getDni());
        return proveedor;
    }
    
    @Override
    public ProveedorDTO convertir_a_DTO(Proveedor proveedor) {
        return new ProveedorDTO(
            proveedor.getPersonaId(), 
            proveedor.getDni(),
            proveedor.getNombre(),
            proveedor.getEdad(),
            proveedor.getProveedorId()
        );
    }

    // METODOS ------------------------------------------------------------------------------------------------------------------------------------------------
    
    // GET
    @Override
    public Set<ProveedorDTO> obtenerTodos() {
        return proveedorRepositorio.findAll().stream() // JPA automáticamente hace el JOIN entre las tablas 'personas' y 'proveedores'.
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<ProveedorDTO> buscarPorID(Long id) {
        validarIdValido(id);
        return proveedorRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<ProveedorDTO> buscarPorDNI(int dni) {
        return proveedorRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    @Override
    public Set<ProveedorDTO> filtrar(String campo, Object valor) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de filtrado");
        }
        if (valor == null) {
            throw new CampoRequeridoException("valor de filtrado");
        }

        Stream<Proveedor> stream = proveedorRepositorio.findAll().stream();

        Predicate<Proveedor> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equals(valor);
            case "edad" -> {
                 try {
                     int edad = Integer.parseInt(valor.toString());
                     filtro = p -> p.getEdad().equals(edad);
                 } catch (NumberFormatException e) {
                     throw new IngresoInvalidoException("valor", "debe ser un número entero para el campo 'edad'");
                 }
            }
            case "dni" -> {
                try {
                    int dni = Integer.parseInt(valor.toString());
                    filtro = p -> p.getDni() == dni;
                } catch (NumberFormatException e) {
                    throw new IngresoInvalidoException("valor", "debe ser un número entero para el campo 'dni'");
                }
            }
            default -> throw new IngresoInvalidoException(
                "campo de filtrado",
                "debe ser 'nombre', 'edad' o 'dni'"
            );
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<ProveedorDTO> ordenar(String campo, boolean ascendente) {
        if (campo == null || campo.trim().isEmpty()) {
            throw new CampoRequeridoException("campo de ordenamiento");
        }
        Stream<Proveedor> stream = proveedorRepositorio.findAll().stream();

        Comparator<Proveedor> comparador;
        switch (campo.toLowerCase()) {
            case "nombre"-> comparador = Comparator.comparing(Proveedor::getNombre);
            case "edad"-> comparador = Comparator.comparing(Proveedor::getEdad);
            case "dni"-> comparador = Comparator.comparing(Proveedor::getDni);
            default -> comparador = Comparator.comparing(Proveedor::getProveedorId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    public boolean cargar(FormPersonaDTO createDTO) {
        validarDatosProveedor(createDTO);
        validarProveedorNoExiste(createDTO.getDni());
        // JPA automáticamente realiza el INSERT en la tabla 'personas' y luego en 'proveedors'
        proveedorRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormPersonaDTO updateDTO) {
        validarDatosProveedor(updateDTO);

        Proveedor proveedor = obtenerProveedorPorId(id);
        
        validarProveedorNoExisteOtro(updateDTO.getDni(), proveedor.getPersonaId()); 
        
        proveedor.setNombre(updateDTO.getNombre().trim()); 
        proveedor.setEdad(updateDTO.getEdad());
        proveedor.setDni(updateDTO.getDni());

        proveedorRepositorio.save(proveedor);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Proveedor proveedor = obtenerProveedorPorId(id);
        proveedorRepositorio.delete(proveedor);
        return true;
    }

    // VALIDACIONES PRIVADAS ================================================================================================================================== [cite: 44]

    /**
     * @param id El ID a validar.
     * @throws IngresoInvalidoException si el ID es nulo o no positivo.
     */
    private void validarIdValido(Long id) { 
        if (id == null || id <= 0) { 
            throw new IngresoInvalidoException("ID", "debe ser un número positivo para la entidad " + ENTIDAD);
        }
    }

    /** @param dto El DTO a validar. @throws IngresoInvalidoException si el DTO es nulo.
        @throws CampoRequeridoException si algún campo obligatorio es nulo o vacío. */
    private void validarDatosProveedor(FormPersonaDTO dto) { 
        if (dto == null) { 
            throw new IngresoInvalidoException("Los datos de " + ENTIDAD + " no pueden ser nulos");
        }
        
        // Validación de campos obligatorios
        if (dto.getDni() <= 0) { 
             // Usamos IngresoInvalidoException para el formato o rango del DNI
             throw new IngresoInvalidoException("DNI", "debe ser un número positivo.");
        }
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new CampoRequeridoException("nombre");
        }
        if (dto.getEdad() == null || dto.getEdad() <= 0) { 
            throw new CampoRequeridoException("edad"); 
        }
    }

    /** @param dni El DNI a verificar.
        @throws EntidadDuplicadaException si ya existe un Proveedor con ese DNI. 
     */
    private void validarProveedorNoExiste(int dni) { 
        // Usamos el método existente 'buscarPorDNI' para chequear
        Optional<ProveedorDTO> existente = this.buscarPorDNI(dni); 
        if (existente.isPresent()) {
            throw new EntidadDuplicadaException(ENTIDAD, "dni", dni);
        }
    }

    /** @param dni El DNI a verificar. @param id El ID de la persona (PersonaId) del proveedor que se está actualizando.
        @throws EntidadDuplicadaException si ya existe otro Proveedor con ese DNI. */
    private void validarProveedorNoExisteOtro(int dni, Long id) {
        Optional<Proveedor> existente = proveedorRepositorio.findByPersona_Dni(dni);
        // Si existe y su ID de persona es diferente al que estamos actualizando, es duplicado. 
        if (existente.isPresent() && !existente.get().getPersonaId().equals(id)) {
            throw new EntidadDuplicadaException(ENTIDAD, "dni", dni); 
        }
    }

    /** @param id El ID de la entidad a buscar.
        @return La entidad Proveedor encontrada.
        @throws EntidadNoEncontradaException si no se encuentra la entidad. */
    private Proveedor obtenerProveedorPorId(Long id) {
        validarIdValido(id); 
        return proveedorRepositorio.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException(ENTIDAD, id));
    }
}
