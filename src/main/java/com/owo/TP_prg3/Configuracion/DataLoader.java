package com.owo.TP_prg3.Configuracion;


import com.owo.TP_prg3.Clases.Entidad.modelo.Entidad;
import com.owo.TP_prg3.Clases.Entidad.modelo.EntidadRepositorio;
import com.owo.TP_prg3.Clases.Entidad.modelo.TipoEntidad;
import com.owo.TP_prg3.VistaConsola.User.dto.CreateUsuarioDTO;
import com.owo.TP_prg3.VistaConsola.User.modelo.UsuarioRepositorio;
import com.owo.TP_prg3.VistaConsola.User.service.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataLoader es un componente de Spring Boot que se ejecuta una vez que la aplicación
 * ha iniciado completamente. Se utiliza aquí para poblar la base de datos con un
 * usuario administrador inicial si no existen usuarios.
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EntidadRepositorio entidadRepositorio;

    @Override
    public void run(String... args) throws Exception {
        // Solo crea un usuario si no hay ninguno en la base de datos
        if (usuarioRepositorio.count() == 0) {
            System.out.println("No se encontraron usuarios. Creando usuario administrador inicial...");

            Integer adminDni = 123; // DNI del administrador inicial
            String adminPassword = "0000"; // Contraseña temporal

            // 1. Verificar si ya existe una entidad con el DNI del administrador, si no, crearla.
            if (!entidadRepositorio.findByDni(adminDni).isPresent()) {
                Entidad adminEntidad = new Entidad();
                adminEntidad.setDni(adminDni);
                adminEntidad.setNombre("Administrador Inicial");
                adminEntidad.setTipoEntidad(TipoEntidad.ADMIN);
                adminEntidad.setEdad(0);
                adminEntidad.setRol("ADMIN");
                entidadRepositorio.save(adminEntidad);
                System.out.println("Entidad ADMINISTRADOR creada para DNI: " + adminDni);
            }

            // 2. Crear el usuario si no existe ya un usuario con ese DNI.
            if (!usuarioRepositorio.existsByDni(adminDni)) {
                CreateUsuarioDTO adminDto = new CreateUsuarioDTO();
                adminDto.setDni(adminDni);
                adminDto.setPassword(adminPassword);

                try {
                    usuarioServicio.crearUsuario(adminDto);
                    System.out.println("Usuario ADMINISTRADOR '" + adminPassword + "' creado con DNI: " + adminDni);
                } catch (RuntimeException e) {
                    System.err.println("Error al crear el usuario administrador inicial: " + e.getMessage());
                }
            } else {
                System.out.println("El usuario con DNI " + adminDni + " ya existe. No se creará un nuevo administrador inicial.");
            }
        } else {
            System.out.println("Ya existen usuarios en la base de datos. No se requiere la creación de un administrador inicial.");
        }
    }
}