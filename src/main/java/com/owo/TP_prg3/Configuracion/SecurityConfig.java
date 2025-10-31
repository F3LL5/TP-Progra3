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
                .authorizeHttpRequests(authorize -> authorize

                        // ADMIN: acceso total a cualquier endpoint.
                        .requestMatchers("/api/**").hasRole("ADMIN")

                        // Permite el acceso al endpoint de perfil para cualquier usuario autenticado
                        .requestMatchers("/api/auth/profile").authenticated()

                        // Puestos: Solo puede modificar su propio puesto (PATCH).
                        .requestMatchers(HttpMethod.PATCH, "/api/puestos/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/puestos/dni/*").hasAnyRole("DUENO_PUESTO", "ADMIN")

                        // Entidades
                        .requestMatchers("/api/entidades/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers("/api/entidades/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers("/api/entidades/clientesConPedidosPuesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/entidades/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/entidades/puesto/*/v2").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/entidades/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/entidades/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/entidades/dni/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/entidades/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/entidades/filtrarYOrdenar*").hasAnyRole("DUENO_PUESTO", "ADMIN")

                        // Inventario
                        .requestMatchers(HttpMethod.GET, "/api/inventario-puesto").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventario-puesto/{id}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventario-puesto/puesto/{id}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventario-puesto/item/{id}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventario-puesto/filtrarYordenarItemsInventario/").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventario-puesto/obtenerInvConStockBajo/{id}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventario-puesto/obtenerInvConStock/{id}").hasAnyRole("DUENO_PUESTO", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/inventario-puesto").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventario-puesto/{id}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventario-puesto/{id}/puesto/{puestoId}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/inventario-puesto/{id}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/inventario-puesto/{id}/puesto/{puestoId}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventario-puesto/{id}/puesto/{puestoId}").hasAnyRole("DUENO_PUESTO", "ADMIN")

                        // Ítems generales (/api/items) - DUENO_PUESTO no puede modificar ni eliminar
                        .requestMatchers(HttpMethod.GET, "/api/items").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/items/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/items/listado").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/items/filtrarYordenar").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/items").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/items/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/items/*").hasAnyRole("DUENO_PUESTO", "ADMIN")


                        // Cuentas bancarias
                        .requestMatchers("/api/cuentas-bancarias/entidad/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers("/api/cuentas-bancarias/*/entidad/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cuentas-bancarias").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cuentas-bancarias/*").hasAnyRole("DUENO_PUESTO", "ADMIN")

                        // Transacciones
                        .requestMatchers(HttpMethod.GET, "/api/transacciones/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/transacciones/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/transacciones/filtrarYOrdenar*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/transacciones/puesto/*/filtrarYOrdenar*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/transacciones/puesto/").hasAnyRole("DUENO_PUESTO", "ADMIN")

                        // Pedidos
                        .requestMatchers(HttpMethod.GET, "/api/pedidos").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/createPedidoYtransaccion").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pedidos/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/pedidos/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/*/factura").hasAnyRole("DUENO_PUESTO", "ADMIN")


                        // Detalles de Pedido
                        .requestMatchers("/api/detalles-pedido/pedido/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers("/api/detalles-pedido/*/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/detalles-pedido/puesto/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/detalles-pedido/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/detalles-pedido/*").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/detalles-pedido/pedido/{pedidoId}/puesto/{puestoId}").hasAnyRole("DUENO_PUESTO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/detalles-pedido/{pedidoId}/total-venta").hasAnyRole("DUENO_PUESTO", "ADMIN")

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