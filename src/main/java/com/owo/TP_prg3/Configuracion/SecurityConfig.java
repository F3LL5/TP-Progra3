package com.owo.TP_prg3.Configuracion;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.owo.TP_prg3.Clases.Usuario.service.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        
        // Permitir OPTIONS para CORS preflight
        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
        
                                    // --- Endpoints públicos ---
        .requestMatchers(
                "/api/auth/login",
                "/api/auth/register"
        ).permitAll()

        // Perfil del usuario logueado: cualquier rol autenticado
        .requestMatchers("/api/auth/profile").authenticated()

        // =====================================================
        //   USUARIOS / DUEÑOS / EMPLEADOS  (ABM de personas del sistema)
        // =====================================================

        // Lectura (GET) permitida a TODOS los roles
        .requestMatchers(HttpMethod.GET,
                "/api/usuarios/**",
                "/api/empleados/**"
        ).hasAnyRole("ADMIN","DUENIO","EMPLEADO")

        .requestMatchers(HttpMethod.GET,
                    "/api/duenios/**"
        ).hasAnyRole("ADMIN","DUENIO")   

        // Alta / modificación / baja SOLO ADMIN y DUEÑO
        .requestMatchers(HttpMethod.POST,
                "/api/usuarios/**",
                "/api/duenios/**",
                "/api/empleados/**"
        ).hasAnyRole("ADMIN","DUENIO")
        .requestMatchers(HttpMethod.PUT,
                "/api/usuarios/**",
                "/api/duenios/**",
                "/api/empleados/**"
        ).hasAnyRole("ADMIN","DUENIO")
        .requestMatchers(HttpMethod.DELETE,
                "/api/usuarios/**",
                "/api/duenios/**",
                "/api/empleados/**"
        ).hasAnyRole("ADMIN","DUENIO")

        // =====================================================
        //   CUENTAS BANCARIAS
        //   - EMPLEADO: solo puede listar / ver
        //   - ADMIN / DUEÑO: pueden crear / modificar / eliminar
        // =====================================================

        // Lectura para todos los roles
        .requestMatchers(HttpMethod.GET, "/api/cuenta_bancarias/**")
            .hasAnyRole("ADMIN","DUENIO","EMPLEADO")

        // Crear / modificar / eliminar SOLO ADMIN y DUEÑO
        .requestMatchers(HttpMethod.POST, "/api/cuenta_bancarias/**")
            .hasAnyRole("ADMIN","DUENIO")
        .requestMatchers(HttpMethod.PUT, "/api/cuenta_bancarias/**")
            .hasAnyRole("ADMIN","DUENIO")
        .requestMatchers(HttpMethod.DELETE, "/api/cuenta_bancarias/**")
            .hasAnyRole("ADMIN","DUENIO")

        // =====================================================
        //   TIENDAS
        //   - EMPLEADO: sólo puede consultar (GET)
        //   - ADMIN / DUEÑO: alta, modif, baja
        // =====================================================

        .requestMatchers(HttpMethod.GET, "/api/configuracion-tienda/**")
            .hasAnyRole("ADMIN","DUENIO","EMPLEADO")

        .requestMatchers(HttpMethod.POST, "/api/configuracion-tienda/**")
            .hasAnyRole("ADMIN","DUENIO")
        .requestMatchers(HttpMethod.PUT, "/api/configuracion-tienda/**")
            .hasAnyRole("ADMIN","DUENIO")
        .requestMatchers(HttpMethod.DELETE, "/api/configuracion-tienda/**")
            .hasAnyRole("ADMIN","DUENIO")

        // =====================================================
        //   RESTO DE ENTIDADES DEL NEGOCIO
        //   (empleado puede hacer todo: GET/POST/PUT/DELETE)
        // =====================================================

        .requestMatchers(
                "/api/clientes/**",
                "/api/personas/**",
                "/api/productos/**",
                "/api/inventarios/**",
                "/api/pedidos/**",
                "/api/detallespedido/**",
                "/api/proveedores/**",
                "/api/transacciones/**",
                "/api/inventarios/**",
                "/api/lotes/**",   // por si Spring lo normaliza
                "api/lotes/**"     // por cómo está el @RequestMapping en tu LoteControlador
        ).hasAnyRole("ADMIN","DUENIO","EMPLEADO")

        .requestMatchers(
                "/api/historial/**" 
        ).hasAnyRole("ADMIN","DUENIO","EMPLEADO")

        .requestMatchers(
                "/api/stats/**"
        ).hasAnyRole("ADMIN","DUENIO","EMPLEADO")

        // Cualquier otra cosa que no matchea lo de arriba → prohibido
        .anyRequest().denyAll()
       
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir cualquier origen (desarrollo)
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); 
        
        // Es necesario permitir los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permitir el encabezado Authorization para Basic Auth y otros
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        
        // Es necesario para que el navegador envíe cookies y, en este caso, el encabezado Authorization
        configuration.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}