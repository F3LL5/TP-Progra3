package com.owo.TP_prg3.Configuracion;

import com.owo.TP_prg3.Clases.User.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;


@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        authorize -> authorize
                                // Permite el acceso al endpoint de perfil para cualquier usuario autenticado
                                .requestMatchers("/api/auth/profile").authenticated()

                                // Acceso para el Rol DUENO_PUESTO

                                // Puestos: Solo puede modificar su propio puesto (PATCH).
                                .requestMatchers(HttpMethod.PATCH, "/api/puestos/*").hasRole("DUENO_PUESTO")

                                // Entidades: Gestión de entidades (clientes y proveedores) de SU puesto.
                                .requestMatchers("/api/entidades/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/entidades/*/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/entidades/clientesConPedidosPuesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.POST, "/api/entidades/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.DELETE, "/api/entidades/*/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.PATCH, "/api/entidades/*/puesto/*").hasRole("DUENO_PUESTO")


                                // Inventario de Puesto (Ítems): Acceso total para los ítems de SU inventario/puesto.
                                .requestMatchers("/api/inventario-puesto/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/inventario-puesto/*/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.POST, "/api/inventario-puesto").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.DELETE, "/api/inventario-puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.PATCH, "/api/inventario-puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/inventario-puesto/obtenerInvConStock/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/inventario-puesto/obtenerInvConStockBajo/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/inventario-puesto/filtrarYordenarItemsInventario*").hasRole("DUENO_PUESTO")

                                // Cuentas Bancarias: Solo ver y gestionar SU propia cuenta (asociada a su entidad).
                                .requestMatchers("/api/cuentas-bancarias/entidad/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/cuentas-bancarias/*/entidad/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.POST, "/api/cuentas-bancarias").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.DELETE, "/api/cuentas-bancarias/*").hasRole("DUENO_PUESTO")


                                // Transacciones: Solo GET para las transacciones de SU puesto. No puede agregar/eliminar/modificar.
                                .requestMatchers(HttpMethod.GET, "/api/transacciones/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.GET, "/api/transacciones/*/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.GET, "/api/transacciones/filtrarYOrdenar*").hasRole("DUENO_PUESTO")


                                // Pedidos: Acceso total para los pedidos de SU puesto.
                                .requestMatchers("/api/pedidos/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/pedidos/*/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.POST, "/api/pedidos").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.DELETE, "/api/pedidos/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.PATCH, "/api/pedidos/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.GET, "/api/pedidos/filtrarYOrdenar*").hasRole("DUENO_PUESTO")


                                // Detalles de Pedido: Acceso total para los detalles de pedido de SU puesto.
                                .requestMatchers("/api/detalles-pedido/pedido/*/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers("/api/detalles-pedido/*/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.POST, "/api/detalles-pedido/puesto/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.DELETE, "/api/detalles-pedido/*").hasRole("DUENO_PUESTO")
                                .requestMatchers(HttpMethod.PATCH, "/api/detalles-pedido/*").hasRole("DUENO_PUESTO")


                                // Acceso para el Rol ADMIN
                                // El rol ADMIN tiene acceso total a CUALQUIER endpoint
                                .requestMatchers("/api/**").hasRole("ADMIN")

                                // Denegar cualquier otra solicitud que no haya sido permitida explícitamente
                                .anyRequest().denyAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .build();
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

