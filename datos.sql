USE c5_proyecto_comercio;

-- ---------------------------------------------------------------------------------------
-- IMPORTANTE: Insertamos IDs explícitos para evitar error 1452 de Claves Foráneas
-- ---------------------------------------------------------------------------------------

-- 1. USUARIOS (10 registros)
INSERT INTO usuarios (usuario_id, email, contraseña, rol) VALUES 
(2, 'waylon.smithers@nuclear.com', '1', 'ROLE_ADMIN'),
(3, 'homero.simpson@nuclear.com', '1', 'ROLE_EMPLEADO'),
(4, 'apu.nahasapeemapetilon@kwik.com', '1', 'ROLE_EMPLEADO'),
(5, 'moe.szyslak@taberna.com', '1', 'ROLE_EMPLEADO'),
(6, 'lionel.messi@afa.org.ar', '1', 'ROLE_ADMIN'),
(7, 'mario.bros@nintendo.com', '1', 'ROLE_EMPLEADO'),
(8, 'kratos@sparta.com', '1', 'ROLE_EMPLEADO'),
(9, 'ricardo.darin@cine.ar', '1', 'ROLE_DUENIO'),
(10, 'mirtha.legrand@mesaza.com', '1', 'ROLE_ADMIN');

-- 2. PERSONAS (29 registros con ID explícito)
INSERT INTO personas (persona_id, nombre, edad, dni) VALUES 
-- Simpsons
(1, 'Charles Montgomery Burns', 104, 1000001),
(2, 'Waylon Smithers', 40, 2000002),
(3, 'Homero Simpson', 39, 3000003),
(4, 'Apu Nahasapeemapetilon', 45, 4000004),
(5, 'Moe Szyslak', 50, 5000005),
(6, 'Marge Simpson', 38, 6000006),
(7, 'Bart Simpson', 10, 7000007),
(8, 'Lisa Simpson', 8, 8000008),
(9, 'Barney Gumble', 40, 9000009),
-- Argentina
(10, 'Lionel Andrés Messi', 36, 30000100),
(11, 'Diego Armando Maradona', 60, 10000100),
(12, 'Ricardo Darín', 67, 11000100),
(13, 'Guillermo Francella', 69, 12000100),
(14, 'Mirtha Legrand', 97, 100000),
(15, 'Susana Giménez', 80, 4000100),
(16, 'Moria Casán', 77, 5000100),
(17, 'Fito Páez', 61, 14000100),
(18, 'Charly García', 72, 15000100),
(19, 'Pity Alvarez', 50, 16000100),
-- Videojuegos
(20, 'Mario Mario', 40, 8880001),
(21, 'Luigi Mario', 38, 8880002),
(22, 'Princesa Peach', 25, 8880003),
(23, 'Link Hyrule', 117, 9990001),
(24, 'Zelda Hyrule', 117, 9990002),
(25, 'Kratos Spartan', 1050, 6660001),
(26, 'Solid Snake', 42, 7770001),
(27, 'Lara Croft', 30, 5550001),
(28, 'Master Chief', 45, 1170001),
(29, 'Gordon Freeman', 27, 3330001);

-- 3. ROLES ESPECÍFICOS (Referencias seguras por ID)

-- DUENIOS
INSERT INTO duenios (persona_id, usuario_id) VALUES 
(1, 1),   -- Burns
(12, 9);  -- Darín

-- EMPLEADOS
INSERT INTO empleados (persona_id, usuario_id) VALUES 
(2, 2),   -- Smithers
(3, 3),   -- Homero
(4, 4),   -- Apu
(5, 5),   -- Moe
(20, 7),  -- Mario
(25, 8);  -- Kratos

-- CLIENTES
INSERT INTO clientes (persona_id) VALUES 
(6), (7), (8), (9), -- Familia Simpson y Barney
(10), (11), (13), (15), (16), (17), (18), -- Famosos Arg
(21), (22), (23), (24), (26), (27), (28), (29); -- Personajes Juegos

-- PROVEEDORES
INSERT INTO proveedores (persona_id) VALUES 
(14), -- Mirtha
(19), -- Pity (proveedor rústico)
(4),  -- Apu
(20); -- Mario

-- 4. TIENDAS Y CUENTAS
INSERT INTO tiendas (tienda_id, nombre, direccion, caja, duenio_id) VALUES 
(1, 'Kwik-E-Mart', 'Av. Siempre Viva 742', 50000, 1),
(2, 'Cine Nacional', 'Av. Corrientes 1234', 100000, 2);

INSERT INTO cuenta_bancarias (tienda_id, cbu, saldo) VALUES 
(1, 12345, 1000000),
(2, 98765, 5000000);

-- 5. PRODUCTOS (Con ID explícito)
INSERT INTO productos (producto_id, nombre, categoria, producto_imagen) VALUES 
(1, 'Rosquilla Rosada', 'Comida', 'donut.jpg'),
(2, 'Cerveza Duff', 'Comida', 'duff.jpg'),
(3, 'Krusty Burger', 'Comida', 'burger.jpg'),
(4, 'Squishee', 'Comida', 'slushie.jpg'),
(5, 'Mate de Plata', 'Bebida', 'mate.jpg'),
(6, 'Fernet Branca', 'Bebida', 'fernet.jpg'),
(7, 'Alfajor Havanna', 'Comida', 'alfajor.jpg'),
(8, 'Choripan', 'Comida', 'chori.jpg'),
(9, 'Hongo Rojo', 'Poderes', 'mushroom.jpg'),
(10, 'Estrella de Invencibilidad', 'Poderes', 'star.jpg'),
(11, 'Espada Maestra', 'Armas', 'sword.jpg'),
(12, 'Ocarina del Tiempo', 'Instrumentos', 'ocarina.jpg'),
(13, 'Rifle de Asalto Lancer', 'Armas', 'lancer.jpg'),
(14, 'Poción de Salud', 'Pociones', 'potion.jpg'),
(15, 'Pip-Boy 3000', 'Electrónica', 'pipboy.jpg'),
(16, 'PlayStation 5', 'Electrónica', 'ps5.jpg'),
(17, 'Game Boy Color', 'Electrónica', 'gbc.jpg');

-- 6. INVENTARIOS Y LOTES
INSERT INTO inventarios (cantidad, producto_id, stock_min, precio_venta, costo_adquisicion) VALUES 
(100, 1, 10, 50, 10),
(500, 2, 50, 100, 30),
(200, 3, 20, 150, 50),
(50, 4, 5, 75, 20),
(1000, 5, 10, 5000, 2000),
(300, 6, 30, 8000, 4000),
(600, 7, 50, 1500, 800),
(150, 8, 20, 3000, 1000),
(10, 9, 1, 90000, 1000),
(5, 10, 1, 99999, 5000),
(1, 11, 1, 50000, 100),
(3, 12, 1, 25000, 500),
(50, 13, 5, 40000, 10000),
(200, 14, 20, 500, 100),
(15, 15, 2, 65000, 20000),
(20, 16, 5, 900000, 700000),
(10, 17, 2, 15000, 5000);

INSERT INTO lotes (producto_id, cantidad_disponible, costo_unitario, fecha_ingreso) VALUES 
(1, 50, 10, '2023-01-01'),
(1, 50, 10, '2023-01-02'),
(2, 250, 30, '2023-02-01'),
(2, 250, 30, '2023-02-15'),
(6, 300, 4000, '2023-10-10'),
(16, 20, 700000, '2023-11-01');

-- 7. TRANSACCIONES Y PEDIDOS (IDs explícitos para conectar detalles)

-- Transacción 1
INSERT INTO transacciones (transaccion_id, tipo, monto, origen_id, destino_id) VALUES (1, 'DEBITO', 500000, 1, 20); 
INSERT INTO pedidos (pedido_id, transaccion_id, tipo) VALUES (1, 1, 'COMPRA');

-- Transacción 2
INSERT INTO transacciones (transaccion_id, tipo, monto, origen_id, destino_id) VALUES (2, 'EFECTIVO', 150000, 3, 1); 
INSERT INTO pedidos (pedido_id, transaccion_id, tipo) VALUES (2, 2, 'VENTA');

-- Transacción 3
INSERT INTO transacciones (transaccion_id, tipo, monto, origen_id, destino_id) VALUES (3, 'EFECTIVO', 900000, 10, 1); 
INSERT INTO pedidos (pedido_id, transaccion_id, tipo) VALUES (3, 3, 'VENTA');

-- Transacción 4
INSERT INTO transacciones (transaccion_id, tipo, monto, origen_id, destino_id) VALUES (4, 'DEBITO', 55000, 21, 1); 
INSERT INTO pedidos (pedido_id, transaccion_id, tipo) VALUES (4, 4, 'VENTA');

-- Transacción 5
INSERT INTO transacciones (transaccion_id, tipo, monto, origen_id, destino_id) VALUES (5, 'EFECTIVO', 2000000, 14, 1); 
INSERT INTO pedidos (pedido_id, transaccion_id, tipo) VALUES (5, 5, 'VENTA');

-- Transacción 6
INSERT INTO transacciones (transaccion_id, tipo, monto, origen_id, destino_id) VALUES (6, 'DEBITO', 100000, 1, 19); 
INSERT INTO pedidos (pedido_id, transaccion_id, tipo) VALUES (6, 6, 'COMPRA');

-- 8. DETALLES DE PEDIDOS
-- Ahora es 100% seguro usar pedido_id 1, 2, etc. porque los forzamos arriba.

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, subtotal) VALUES 
-- Pedido 1
(1, 9, 10, 10000),
(1, 10, 5, 25000),
(1, 11, 1, 100),
(1, 14, 100, 10000),
-- Pedido 2
(2, 1, 12, 600),
(2, 2, 24, 2400),
(2, 3, 5, 750),
(2, 8, 3, 9000),
(2, 17, 1, 15000),
(2, 7, 6, 9000),
-- Pedido 3
(3, 5, 1, 5000),
(3, 6, 10, 80000),
(3, 8, 50, 150000),
(3, 7, 20, 30000),
(3, 16, 1, 900000),
-- Pedido 4
(4, 14, 10, 5000),
(4, 11, 1, 50000),
(4, 9, 1, 90000),
-- Pedido 5
(5, 15, 1, 65000),
(5, 12, 1, 25000),
(5, 1, 100, 5000),
(5, 5, 5, 25000),
(5, 16, 2, 1800000),
-- Pedido 6
(6, 2, 100, 3000),
(6, 6, 50, 200000),
(6, 7, 50, 40000),
(6, 14, 20, 2000);