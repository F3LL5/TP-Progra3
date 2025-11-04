package com.owo.TP_prg3.Clases.Empleado.service;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.owo.TP_prg3.Clases.Empleado.dto.EmpleadoDTO;
import com.owo.TP_prg3.Clases.Empleado.dto.FormEmpleadoDTO;
import com.owo.TP_prg3.Clases.Empleado.modelo.Empleado;
import com.owo.TP_prg3.Clases.Empleado.modelo.EmpleadoRepositorio;
import com.owo.TP_prg3.Clases.Interfaces.I_CRUD;
import com.owo.TP_prg3.Clases.Usuario.modelo.RolUsuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;

@Service
public class EmpleadoServicio implements I_CRUD<Empleado, EmpleadoDTO, FormEmpleadoDTO> {

    @Autowired
    private EmpleadoRepositorio empleadoRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Conversion
    @Override
    public Empleado convertir_a_Obj(FormEmpleadoDTO fDTO) {
        Empleado empleado = new Empleado();
        empleado.setNombre(fDTO.getNombre());
        empleado.setEdad(fDTO.getEdad());
        empleado.setDni(fDTO.getDni());
        Usuario usuario = new Usuario(null, fDTO.getEmail(), passwordEncoder.encode(fDTO.getContraseña()), RolUsuario.ROLE_EMPLEADO);
        empleado.setUsuario(usuario);
        return empleado;
    }

    @Override
    public EmpleadoDTO convertir_a_DTO(Empleado empleado) {
        return new EmpleadoDTO(
            empleado.getPersonaId(), 
            empleado.getDni(),
            empleado.getNombre(),
            empleado.getEdad(),
            empleado.getEmpleadoId(),
            empleado.getUsuario().getEmail()
        );
    }

    @Override
    public Set<EmpleadoDTO> obtenerTodos() {
        return empleadoRepositorio.findAll().stream() // JPA automáticamente hace el JOIN entre las tablas 'personas' y 'empleados'.
            .map(this::convertir_a_DTO)
            .collect(Collectors.toSet());
    }

    @Override
    public Optional<EmpleadoDTO> buscarPorID(Long id) {
        return empleadoRepositorio.findById(id).map(this::convertir_a_DTO);
    }

    public Optional<EmpleadoDTO> buscarPorDNI(int dni) {
        return empleadoRepositorio.findByPersona_Dni(dni).map(this::convertir_a_DTO);
    }

    @Override
    public Set<EmpleadoDTO> filtrar(String campo, Object valor) {
        Stream<Empleado> stream = empleadoRepositorio.findAll().stream();

        Predicate<Empleado> filtro;
        switch (campo.toLowerCase()) {
            case "nombre"-> filtro = p -> p.getNombre().equals(valor);
            case "edad" -> filtro = p -> p.getEdad().equals(valor);
            default -> filtro = p -> false;
        }

        return stream.filter(filtro)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    public Set<EmpleadoDTO> ordenar(String campo, boolean ascendente) {
        Stream<Empleado> stream = empleadoRepositorio.findAll().stream();

        Comparator<Empleado> comparador;
        switch (campo.toLowerCase()) {
            case "nombre"-> comparador = Comparator.comparing(Empleado::getNombre);
            case "edad"-> comparador = Comparator.comparing(Empleado::getEdad);
            case "dni"-> comparador = Comparator.comparing(Empleado::getDni);
            default -> comparador = Comparator.comparing(Empleado::getEmpleadoId);
        }
        if (!ascendente) comparador = comparador.reversed();

        return stream.sorted(comparador)
                    .map(this::convertir_a_DTO)
                    .collect(Collectors.toSet());
    }

    // POST
    @Override
    public boolean cargar(FormEmpleadoDTO createDTO) {
        if (this.buscarPorDNI(createDTO.getDni()).isPresent()) return false;
        empleadoRepositorio.save(convertir_a_Obj(createDTO));
        return true;
    }

    // PUT
   @Override
    public boolean actualizar(Long id, FormEmpleadoDTO updateDTO) {
        Optional<Empleado> optional = empleadoRepositorio.findById(id);
        if (optional.isEmpty()) return false;
        
        Empleado Empleado = optional.get();
        Empleado.setNombre(updateDTO.getNombre());
        Empleado.setEdad(updateDTO.getEdad());
        Empleado.setDni(updateDTO.getDni());

        empleadoRepositorio.save(Empleado);
        return true;
    }

    // DELETE 
    @Override
    public boolean eliminar(Long id) {
        Optional<Empleado> optional = empleadoRepositorio.findById(id);
        if(optional.isEmpty()) return false;
        else empleadoRepositorio.delete(optional.get());
        return true;
    }

}
