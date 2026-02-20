package com.owo.TP_prg3.Configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.owo.TP_prg3.Clases.Enum.RolUsuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.Usuario;
import com.owo.TP_prg3.Clases.Usuario.modelo.UsuarioRepositorio;

@Component
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    // Esta clase sirve para inicializar el sistema, si no hay ningún dato crea un usuario administrador default.

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    DataLoader(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Solo crea un usuario si no hay ninguno en la base de datos
        if (usuarioRepositorio.count() == 0) {
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
            System.out.println();
            System.out.println("No se encontraron usuarios. Creando usuario administrador inicial...");

            String adminMail = "123";
            String adminPassword = "0000"; 
            String encodedPassword = passwordEncoder.encode(adminPassword);

            Usuario usuario = new Usuario(
                null,
                adminMail,
                encodedPassword,
                RolUsuario.ROLE_ADMIN
            );
            usuarioRepositorio.save(usuario);

            System.out.println("Usuario ADMINISTRADOR '" + adminPassword + "' creado con email: " + adminMail);
            System.out.println("Recomendamos encarecidamente que cambie la contraseña una vez ingresado en el sistema.");
            System.out.println();
            System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
            System.out.println("------------------------------------------------------------------------------------");
        }
    }
}