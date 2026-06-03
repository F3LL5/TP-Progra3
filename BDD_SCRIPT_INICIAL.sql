USE proyecto_comercio;

-- ======================================================================================
-- 1. PERSONA del dueño
-- ======================================================================================
INSERT INTO personas (nombre, apellido, fecha_nacimiento, dni)
VALUES ('Abraham', 'Simpson', '1926-04-01', 12345678);

-- ======================================================================================
-- 2. USUARIO del dueño
-- ======================================================================================
INSERT INTO usuarios (email, contraseña, rol)
VALUES ('123', '$2a$10$I1uXCIrxExQMhc2b9dJZjuWo5e0wLEjldvTrdp3R25JVdh/bWueje', 'ROLE_ADMIN');

INSERT INTO usuarios (email, contraseña, rol)
VALUES ('abe.simpson@springfield.com', '$2a$10$I1uXCIrxExQMhc2b9dJZjuWo5e0wLEjldvTrdp3R25JVdh/bWueje', 'ROLE_DUENIO');

-- ======================================================================================
-- 3. DUEÑO
-- ======================================================================================
INSERT INTO duenios (persona_id, usuario_id)
VALUES (1, 2);

-- ======================================================================================
-- 4. TIENDA
-- ======================================================================================
INSERT INTO tiendas (
    razon_social, nombre_fantasia, cuit, condicion_iva,
    ingresos_brutos, fecha_inicio_actividades, punto_de_venta,
    caja,
    dir_calle, dir_altura, dir_piso, dir_cp, dir_localidad, dir_provincia, dir_pais
)
VALUES (
    'Tiendita S.A.', 'Tiendita', 20123456789, 'ResponsableInscripto',
    'Convenio Multilateral', '2024-01-01', 1,
    150.00,
    'Calle Siempre Viva', '742', 'PB', '1234', 'Springfield', 'Buenos Aires', 'Argentina'
);

-- ======================================================================================
-- 5. CUENTA BANCARIA
-- ======================================================================================
INSERT INTO cuenta_bancarias (tienda_id, nombre_banco, cbu, saldo)
VALUES (1, 'Banco Provincia', 123456789, 250.00);

-- ======================================================================================
-- 6. PROVEEDOR
-- ======================================================================================
INSERT INTO proveedores (
    cuit, razon_social, nombre_fantasia, condicion_iva,
    telefono, email,
    dir_calle, dir_altura, dir_cp, dir_localidad, dir_provincia
)
VALUES (
    30987654321, 'Ropa Springfield S.R.L.', 'Ropa Springfield', 'ResponsableInscripto',
    2235001122, 'ventas@ropaspringfield.com',
    'Av. Quimby', '100', '5678', 'Springfield', 'Buenos Aires'
);

-- ======================================================================================
-- 7. PERSONA y CLIENTE
-- ======================================================================================
INSERT INTO personas (nombre, apellido, fecha_nacimiento, dni)
VALUES ('Bart', 'Simpson', '1980-04-01', 87654321);

INSERT INTO clientes (persona_id)
VALUES (2);

-- ======================================================================================
-- 8. PRODUCTOS
-- ======================================================================================
INSERT INTO productos (nombre, categoria)
VALUES
    ('Remera Blanca', 'Indumentaria'),
    ('Pantalon Jean', 'Indumentaria'),
    ('Medias Deportivas', 'Indumentaria');

-- ======================================================================================
-- 9. INVENTARIOS
-- ======================================================================================
INSERT INTO inventarios (cantidad, producto_id, stock_min, precio_venta, costo_adquisicion)
VALUES
    (50, 1, 5,  120.00, 100.00),
    (30, 2, 3,  500.00, 380.00),
    (100, 3, 10,  80.00,  50.00);

-- ======================================================================================
-- 10. LOTES
-- ======================================================================================
INSERT INTO lotes (producto_id, cantidad_disponible, costo_unitario, fecha_ingreso)
VALUES
    (1, 50,  100.00, '2024-03-01'),
    (2, 30,  380.00, '2024-03-01'),
    (3, 100,  50.00, '2024-03-01');

-- ======================================================================================
-- MOVIMIENTOS
-- ======================================================================================

-- --------------------------------------------------------------------------------------
-- Mov 1: COMPRA 5 remeras — TRANSFERENCIA (sale de cuenta bancaria)
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('TRANSFERENCIA', '2024-03-05 10:00:00', 500.00, 1, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (1, 'COMPRA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES (1, 1, 5, 500.00);

-- --------------------------------------------------------------------------------------
-- Mov 2: VENTA 1 remera — EFECTIVO (entra a caja) +120
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('EFECTIVO', '2024-03-06 11:30:00', 120.00, NULL, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (2, 'VENTA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES (2, 1, 1, 120.00);

-- --------------------------------------------------------------------------------------
-- Mov 3: COMPRA 10 medias — EFECTIVO (sale de caja) -210
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('EFECTIVO', '2024-03-08 09:00:00', 210.00, 1, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (3, 'COMPRA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES (3, 3, 10, 210.00);

-- --------------------------------------------------------------------------------------
-- Mov 4: VENTA 3 medias — EFECTIVO (entra a caja) +240
-- Caja: 120 - 210 + 240 = 150 ✓
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('EFECTIVO', '2024-03-10 14:00:00', 240.00, NULL, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (4, 'VENTA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES (4, 3, 3, 240.00);

-- --------------------------------------------------------------------------------------
-- Mov 5: COMPRA 2 pantalones — TRANSFERENCIA (sale de cuenta) -760
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('TRANSFERENCIA', '2024-03-12 10:00:00', 760.00, 1, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (5, 'COMPRA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES (5, 2, 2, 760.00);

-- --------------------------------------------------------------------------------------
-- Mov 6: VENTA 1 pantalon — TRANSFERENCIA (entra a cuenta) +500
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('TRANSFERENCIA', '2024-03-13 16:00:00', 500.00, NULL, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (6, 'VENTA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES (6, 2, 1, 500.00);

-- --------------------------------------------------------------------------------------
-- Mov 7: VENTA 2 remeras — TRANSFERENCIA (entra a cuenta) +240
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('TRANSFERENCIA', '2024-03-15 12:00:00', 240.00, NULL, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (7, 'VENTA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES (7, 1, 2, 240.00);

-- --------------------------------------------------------------------------------------
-- Mov 8: COMPRA 3 remeras — TRANSFERENCIA (sale de cuenta) -300
-- Cuenta: -500 -760 +500 +240 -300 = -820 → falta +1070 con mov9
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('TRANSFERENCIA', '2024-03-18 09:30:00', 300.00, 1, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (8, 'COMPRA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES (8, 1, 3, 300.00);

-- --------------------------------------------------------------------------------------
-- Mov 9: VENTA 1 remera + 2 medias — TRANSFERENCIA (entra a cuenta) +1070
-- Cuenta: -500 -760 +500 +240 -300 +1070 = +250 ✓
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('TRANSFERENCIA', '2024-03-20 17:00:00', 1070.00, NULL, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (9, 'VENTA', 'FINALIZADO');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES
    (9, 1, 1, 870.00),
    (9, 3, 2, 200.00);

-- --------------------------------------------------------------------------------------
-- Mov 10: COMPRA 5 medias + 1 pantalon — PENDIENTE (no afecta saldos)
-- --------------------------------------------------------------------------------------
INSERT INTO transacciones (tipo, fecha, monto, origen_id, destino_id)
VALUES ('TRANSFERENCIA', '2024-03-22 08:00:00', 630.00, 1, 1);

INSERT INTO pedidos (transaccion_id, tipo, estado)
VALUES (10, 'COMPRA', 'PENDIENTE');

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal)
VALUES
    (10, 3, 5, 250.00),
    (10, 2, 1, 380.00);