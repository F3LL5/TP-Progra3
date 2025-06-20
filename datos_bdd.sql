-- #####################################################################
-- 1. Inserción en la tabla 'items'
-- #####################################################################
INSERT INTO items (item_id, nombre, categoria) VALUES
-- Items Originales
(1, 'Manzana Fuji', 'Frutas'),
(2, 'Banana Ecuador', 'Frutas'),
(3, 'Naranja de Jugo', 'Frutas'),
(4, 'Tomate Perita', 'Verduras'),
(5, 'Lechuga Criolla', 'Verduras'),
(6, 'Zanahoria Fresca', 'Verduras'),
(7, 'Pechuga de Pollo (kg)', 'Carnes'),
(8, 'Bife de Chorizo (kg)', 'Carnes'),
(9, 'Salmón Rosado (kg)', 'Pescados'),
(10, 'Queso Pategrás (kg)', 'Lácteos'),
(11, 'Leche Entera (litro)', 'Lácteos'),
(12, 'Yogur de Vainilla', 'Lácteos'),
(13, 'Pan Francés', 'Panadería'),
(14, 'Medialunas de Manteca', 'Panadería'),
(15, 'Gaseosa Cola (2.25L)', 'Bebidas'),
(16, 'Agua Mineral sin Gas (1.5L)', 'Bebidas'),
(17, 'Aceite de Girasol', 'Almacén'),
(18, 'Arroz Largo Fino', 'Almacén'),
(19, 'Fideos Tirabuzón', 'Almacén'),
(20, 'Jabón en Polvo (3kg)', 'Limpieza'),
-- Nuevos Items Argentinos y de Los Simpson
(21, 'Fernet Branca 750ml', 'Bebidas Alcoholicas'),
(22, 'Coca Cola 1.5L', 'Bebidas'),
(23, 'Yerba Mate Taragüi 1kg', 'Almacén'),
(24, 'Dulce de Leche La Serenísima 400g', 'Lácteos'),
(25, 'Alfajor Jorgito Triple de Chocolate', 'Golosinas'),
(26, 'Tira de Asado (kg)', 'Carnes'),
(27, 'Vacío (kg)', 'Carnes'),
(28, 'Chorizo de Cerdo (unidad)', 'Carnes'),
(29, 'Morcilla (unidad)', 'Carnes'),
(30, 'Vino Malbec Trumpeter 750ml', 'Bebidas Alcoholicas'),
(31, 'Cerveza Quilmes (L)', 'Bebidas Alcoholicas'),
(32, 'Empanada de Carne (docena)', 'Comidas Preparadas'),
(33, 'Milanesa de Ternera (kg)', 'Carnes'),
(34, 'Pascualina de Verdura (unidad)', 'Comidas Preparadas'),
(35, 'Cerveza Duff (pack x6)', 'Bebidas Alcoholicas'),
(36, 'Krusty Burger con Queso', 'Comidas Preparadas'),
(37, 'Rosquilla Lard Lad', 'Panadería'),
(38, 'Buzz Cola', 'Bebidas'),
(39, 'Squishee de Cereza Azul', 'Bebidas'),
(40, 'Tomaco', 'Exóticos');

-- #####################################################################
-- 2. Inserción en la tabla 'entidades'
-- #####################################################################
INSERT INTO entidades (entidad_id, nombre, tipo_entidad, rol, edad, dni) VALUES
-- Entidades Originales
(1, 'Carlos Rodriguez', 'Persona Física', 'DUENO_PUESTO', 45, 25123456),
(2, 'Ana Gomez', 'Persona Física', 'DUENO_PUESTO', 38, 28765432),
(3, 'Luis Fernandez', 'Persona Física', 'DUENO_PUESTO', 52, 22890123),
(4, 'Verdulería "El Verde" SRL', 'Persona Jurídica', 'DUENO_PUESTO', 10, 301122334),
(5, 'Panificados "La Espiga"', 'Persona Jurídica', 'DUENO_PUESTO', 15, 305566778),
(6, 'Maria Lopez', 'Persona Física', 'CLIENTE', 29, 35123789),
(7, 'Juan Martinez', 'Persona Física', 'CLIENTE', 34, 32456123),
(8, 'Sofia Torres', 'Persona Física', 'CLIENTE', 22, 41789456),
(9, 'Pedro Sanchez', 'Persona Física', 'CLIENTE', 41, 27890567),
(10, 'Lucia Diaz', 'Persona Física', 'CLIENTE', 55, 18901234),
(11, 'Martin Acosta', 'Persona Física', 'CLIENTE', 31, 34567890),
(12, 'Valeria Ramirez', 'Persona Física', 'CLIENTE', 27, 37890123),
(13, 'Jorge Castillo', 'Persona Física', 'CLIENTE', 60, 14567890),
(14, 'Florencia Rios', 'Persona Física', 'CLIENTE', 25, 39012345),
(15, 'Diego Herrera', 'Persona Física', 'CLIENTE', 39, 29876543),
-- Nuevos DUENO_PUESTOs
(16, 'Homero Simpson', 'Persona Física', 'DUENO_PUESTO', 39, 10000001),
(17, 'Moe Szyslak', 'Persona Física', 'DUENO_PUESTO', 55, 10000002),
(18, 'Apu Nahasapeemapetilon', 'Persona Jurídica', 'DUENO_PUESTO', 48, 10000003),
(19, 'Ricardo Darín', 'Persona Física', 'DUENO_PUESTO', 67, 12345678),
(20, 'Mirtha Legrand', 'Persona Jurídica', 'DUENO_PUESTO', 97, 1),
-- Nuevos CLIENTEs (Simpsons)
(21, 'Marge Simpson', 'Persona Física', 'CLIENTE', 36, 10000004),
(22, 'Bart Simpson', 'Persona Física', 'CLIENTE', 10, 10000005),
(23, 'Lisa Simpson', 'Persona Física', 'CLIENTE', 8, 10000006),
(24, 'Ned Flanders', 'Persona Física', 'CLIENTE', 60, 10000007),
(25, 'C. Montgomery Burns', 'Persona Física', 'CLIENTE', 104, 10000008),
(26, 'Barney Gumble', 'Persona Física', 'CLIENTE', 40, 10000009),
(27, 'Krusty el Payaso', 'Persona Física', 'CLIENTE', 52, 10000010),
(28, 'Milhouse Van Houten', 'Persona Física', 'CLIENTE', 10, 10000011),
(29, 'Waylon Smithers', 'Persona Física', 'CLIENTE', 45, 10000012),
(30, 'Edna Krabappel', 'Persona Física', 'CLIENTE', 41, 10000013),
-- Nuevos CLIENTEs (Argentinos Famosos)
(31, 'Lionel Messi', 'Persona Física', 'CLIENTE', 37, 40123123),
(32, 'Susana Gimenez', 'Persona Física', 'CLIENTE', 80, 2),
(33, 'Marcelo Tinelli', 'Persona Física', 'CLIENTE', 64, 15987654),
(34, 'Guillermo Francella', 'Persona Física', 'CLIENTE', 69, 11223344),
(35, 'Diego Maradona', 'Persona Física', 'CLIENTE', 60, 14934934),
(36, 'Quino', 'Persona Física', 'CLIENTE', 88, 5432109),
(37, 'Jorge Luis Borges', 'Persona Física', 'CLIENTE', 86, 1111111),
(38, 'Mercedes Sosa', 'Persona Física', 'CLIENTE', 74, 3333333),
(39, 'Charly García', 'Persona Física', 'CLIENTE', 72, 10101010),
(40, 'Fito Páez', 'Persona Física', 'CLIENTE', 61, 16161616),
-- Nuevos CLIENTEs (Nombres Comunes Argentinos - 60 CLIENTEs)
(41, 'Facundo Benitez', 'Persona Física', 'CLIENTE', 28, 36123456),
(42, 'Valentina Castro', 'Persona Física', 'CLIENTE', 24, 39876543),
(43, 'Mateo Diaz', 'Persona Física', 'CLIENTE', 31, 34567891),
(44, 'Camila Fernandez', 'Persona Física', 'CLIENTE', 22, 41234567),
(45, 'Bautista Garcia', 'Persona Física', 'CLIENTE', 19, 43876543),
(46, 'Martina Gonzalez', 'Persona Física', 'CLIENTE', 35, 31234567),
(47, 'Joaquin Gomez', 'Persona Física', 'CLIENTE', 42, 26876543),
(48, 'Isabella Lopez', 'Persona Física', 'CLIENTE', 29, 35876543),
(49, 'Felipe Martinez', 'Persona Física', 'CLIENTE', 33, 33234567),
(50, 'Catalina Perez', 'Persona Física', 'CLIENTE', 27, 37876543),
(51, 'Santiago Rodriguez', 'Persona Física', 'CLIENTE', 38, 29874543),
(52, 'Olivia Sanchez', 'Persona Física', 'CLIENTE', 21, 42234567),
(53, 'Benjamin Torres', 'Persona Física', 'CLIENTE', 45, 25876543),
(54, 'Emilia Romero', 'Persona Física', 'CLIENTE', 26, 38876543),
(55, 'Thiago Sosa', 'Persona Física', 'CLIENTE', 30, 35123123),
(56, 'Renata Alvarez', 'Persona Física', 'CLIENTE', 34, 32123123),
(57, 'Nicolas Vazquez', 'Persona Física', 'CLIENTE', 23, 40123124),
(58, 'Julieta Gutierrez', 'Persona Física', 'CLIENTE', 28, 36123125),
(59, 'Lucas Medina', 'Persona Física', 'CLIENTE', 32, 33123126),
(60, 'Victoria Molina', 'Persona Física', 'CLIENTE', 25, 39123127),
(61, 'Agustin Ortiz', 'Persona Física', 'CLIENTE', 29, 35123128),
(62, 'Delfina Ruiz', 'Persona Física', 'CLIENTE', 22, 41123129),
(63, 'Ignacio Silva', 'Persona Física', 'CLIENTE', 36, 30123130),
(64, 'Pilar Dominguez', 'Persona Física', 'CLIENTE', 27, 37123131),
(65, 'Matias Navarro', 'Persona Física', 'CLIENTE', 31, 34123132),
(66, 'Clara Ferreyra', 'Persona Física', 'CLIENTE', 24, 40123133),
(67, 'Lautaro Acosta', 'Persona Física', 'CLIENTE', 20, 42123134),
(68, 'Juana Miranda', 'Persona Física', 'CLIENTE', 39, 28123135),
(69, 'Francisco Blanco', 'Persona Física', 'CLIENTE', 41, 27123136),
(70, 'Sofia Moreno', 'Persona Física', 'CLIENTE', 26, 38123137),
(71, 'Patricio Rey', 'Persona Física', 'CLIENTE', 75, 4567890),
(72, 'Solari Skay', 'Persona Física', 'CLIENTE', 74, 4567891),
(73, 'Luca Prodan', 'Persona Física', 'CLIENTE', 34, 9876543),
(74, 'Gustavo Cerati', 'Persona Física', 'CLIENTE', 55, 17171717),
(75, 'Andres Calamaro', 'Persona Física', 'CLIENTE', 62, 18181818),
(76, 'Maria Elena Walsh', 'Persona Física', 'CLIENTE', 80, 2222222),
(77, 'Julio Cortazar', 'Persona Física', 'CLIENTE', 70, 6666666),
(78, 'Ernesto Sabato', 'Persona Física', 'CLIENTE', 99, 5555555),
(79, 'Mafalda', 'Persona Jurídica', 'CLIENTE', 50, 19641964),
(80, 'Clemente', 'Persona Jurídica', 'CLIENTE', 45, 19731973),
(81, 'Isidoro Cañones', 'Persona Física', 'CLIENTE', 60, 19401940),
(82, 'Patoruzú', 'Persona Física', 'CLIENTE', 85, 19281928),
(83, 'Manuel Belgrano', 'Persona Física', 'CLIENTE', 50, 17701820),
(84, 'José de San Martín', 'Persona Física', 'CLIENTE', 72, 17781850),
(85, 'Juana Azurduy', 'Persona Física', 'CLIENTE', 35, 17801816),
(86, 'Martín Miguel de Güemes', 'Persona Física', 'CLIENTE', 36, 17851821),
(87, 'René Favaloro', 'Persona Física', 'CLIENTE', 77, 19232000),
(88, 'Eva Perón', 'Persona Física', 'CLIENTE', 33, 19191952),
(89, 'Juan Domingo Perón', 'Persona Física', 'CLIENTE', 78, 18951974),
(90, 'Astor Piazzolla', 'Persona Física', 'CLIENTE', 71, 19211992),
(91, 'Anibal Troilo', 'Persona Física', 'CLIENTE', 61, 19141975),
(92, 'Carlos Gardel', 'Persona Física', 'CLIENTE', 44, 18901935),
(93, 'Tita Merello', 'Persona Física', 'CLIENTE', 92, 19021994),
(94, 'Luis Alberto Spinetta', 'Persona Física', 'CLIENTE', 62, 19502012),
(95, 'Atahualpa Yupanqui', 'Persona Física', 'CLIENTE', 84, 19081992),
(96, 'Juan Manuel Fangio', 'Persona Física', 'CLIENTE', 84, 19111995),
(97, 'Guillermo Vilas', 'Persona Física', 'CLIENTE', 71, 19522024),
(98, 'Gabriela Sabatini', 'Persona Física', 'CLIENTE', 54, 19702024),
(99, 'Manu Ginóbili', 'Persona Física', 'CLIENTE', 46, 19772024),
(100, 'Luciana Aymar', 'Persona Física', 'CLIENTE', 46, 19772025);

-- #####################################################################
-- 3. Inserción en la tabla 'puestos'
-- #####################################################################
INSERT INTO puestos (puesto_id, nombre, duenio_id, comision) VALUES
-- Puestos Originales
(1, 'Carnicería Don Carlos', 1, 15.00),
(2, 'Pescadería Anita', 2, 18.50),
(3, 'Almacén Luisito', 3, 12.00),
(4, 'Verdulería El Verde Fresco', 4, 14.00),
(5, 'Panadería La Espiga Dorada', 5, 20.00),
(6, 'Lácteos y Fiambres del Sur', 1, 16.00),
-- Nuevos Puestos
(7, 'Kwik-E-Mart', 18, 10.00), -- DUENO_PUESTO Apu
(8, 'Taberna de Moe', 17, 25.00), -- DUENO_PUESTO Moe
(9, 'Parrilla "El Diez"', 19, 18.00), -- DUENO_PUESTO Darín
(10, 'Almacén "La Chiqui"', 20, 11.50), -- DUENO_PUESTO Mirtha
(11, 'Hamburguesas Krusty', 27, 22.00); -- DUENO_PUESTO Krusty

-- #####################################################################
-- 4. Inserción en la tabla 'inVENTArio_puesto'
-- #####################################################################
INSERT INTO inVENTArio_puesto (inVENTArio_id, puesto_id, item_id, cantidad, stock_min, precio_VENTA, costo_adquisicion) VALUES
-- InVENTArios Originales
(1, 1, 7, 50, 10, 1800.50, 1300.00),
(2, 1, 8, 40, 8, 2500.00, 1900.75),
(3, 2, 9, 30, 5, 3500.00, 2800.00),
(4, 3, 17, 100, 20, 450.00, 350.50),
(5, 3, 18, 120, 30, 280.00, 210.00),
(6, 3, 19, 150, 30, 250.50, 180.25),
(7, 3, 15, 80, 15, 500.00, 410.00),
(8, 3, 16, 90, 20, 180.00, 120.00),
(9, 3, 20, 40, 10, 1200.00, 950.00),
(10, 4, 1, 200, 40, 300.00, 220.00),
(11, 4, 2, 150, 30, 250.00, 190.00),
(12, 4, 3, 100, 25, 200.00, 150.00),
(13, 4, 4, 80, 20, 180.00, 130.00),
(14, 4, 5, 60, 15, 150.00, 110.00),
(15, 4, 6, 90, 20, 120.00, 80.00),
(16, 5, 13, 100, 25, 200.00, 120.00),
(17, 5, 14, 120, 30, 80.00, 50.00),
(18, 6, 10, 30, 8, 2200.00, 1700.00),
(19, 6, 11, 80, 20, 350.00, 280.00),
(20, 6, 12, 70, 15, 280.00, 210.00),
-- InVENTArios Nuevos
-- Kwik-E-Mart (Puesto 7)
(21, 7, 39, 100, 20, 750.00, 500.00), -- Squishee
(22, 7, 38, 100, 20, 480.00, 350.00), -- Buzz Cola
(23, 7, 40, 10, 2, 5000.00, 4500.00), -- Tomaco
(24, 7, 25, 200, 50, 350.00, 250.00), -- Alfajor
(25, 7, 16, 150, 30, 180.00, 120.00), -- Agua
-- Taberna de Moe (Puesto 8)
(26, 8, 35, 300, 50, 2500.00, 1800.00), -- Cerveza Duff
(27, 8, 21, 150, 30, 3000.00, 2200.00), -- Fernet
(28, 8, 22, 200, 40, 500.00, 400.00), -- Coca Cola
(29, 8, 31, 250, 50, 800.00, 550.00), -- Quilmes
-- Parrilla "El Diez" (Puesto 9)
(30, 9, 26, 80, 15, 3800.00, 2900.00), -- Tira de Asado
(31, 9, 27, 70, 15, 4200.00, 3200.00), -- Vacío
(32, 9, 28, 200, 40, 500.00, 350.00), -- Chorizo
(33, 9, 29, 150, 30, 450.00, 300.00), -- Morcilla
(34, 9, 30, 100, 20, 4500.00, 3500.00), -- Vino Malbec
-- Almacén "La Chiqui" (Puesto 10)
(35, 10, 23, 100, 20, 1200.00, 900.00), -- Yerba
(36, 10, 24, 150, 30, 950.00, 700.00), -- Dulce de Leche
(37, 10, 25, 300, 50, 350.00, 250.00), -- Alfajor Jorgito
(38, 10, 32, 50, 10, 4000.00, 3000.00), -- Empanadas
(39, 10, 34, 40, 8, 2500.00, 1800.00), -- Pascualina
-- Hamburguesas Krusty (Puesto 11)
(40, 11, 36, 500, 100, 1500.00, 900.00), -- Krusty Burger
(41, 11, 37, 400, 80, 500.00, 300.00), -- Rosquilla
(42, 11, 38, 300, 60, 480.00, 350.00), -- Buzz Cola
(43, 11, 35, 200, 40, 2500.00, 1800.00); -- Cerveza Duff

-- #####################################################################
-- 5. Inserción en la tabla 'cuenta_bancaria'
-- #####################################################################
INSERT INTO cuenta_bancaria (cuenta_bancaria_id, entidad_id, saldo) VALUES
-- Cuentas Originales
(1, 1, 500000.00), (2, 2, 350000.00), (3, 3, 780000.00), (4, 4, 1200000.00), (5, 5, 950000.00),
(6, 6, 50000.00), (7, 7, 75000.00), (8, 8, 30000.00), (9, 9, 120000.00), (10, 10, 95000.00),
(11, 11, 45000.00), (12, 12, 60000.00), (13, 13, 250000.00), (14, 14, 80000.00), (15, 15, 15000.00),
-- Cuentas Nuevos DUENO_PUESTOs
(16, 16, 25000.00), (17, 17, 150000.00), (18, 18, 300000.00), (19, 19, 15000000.00), (20, 20, 100000.00),
-- Cuentas Nuevos CLIENTEs (IDs 21 a 100)
(21, 21, 40000.00), (22, 22, 5000.00), (23, 23, 8000.00), (24, 24, 120000.00), (25, 25, 999999.00),
(26, 26, 1500.00), (27, 27, 5000000.00), (28, 28, 4500.00), (29, 29, 80000.00), (30, 30, 60000.00),
(31, 31, 250000.00), (32, 32, 5000000.00), (33, 33, 300000.00), (34, 34, 25000000.00), (35, 35, 000000.00),
(36, 36, 500000.00), (37, 37, 450000.00), (38, 38, 700000.00), (39, 39, 8000000.00), (40, 40, 6000000.00),
(41, 41, 35000.00), (42, 42, 42000.00), (43, 43, 55000.00), (44, 44, 28000.00), (45, 45, 21000.00),
(46, 46, 68000.00), (47, 47, 81000.00), (48, 48, 47000.00), (49, 49, 61000.00), (50, 50, 53000.00),
(51, 51, 71000.00), (52, 52, 33000.00), (53, 53, 91000.00), (54, 54, 48000.00), (55, 55, 59000.00),
(56, 56, 64000.00), (57, 57, 41000.00), (58, 58, 50000.00), (59, 59, 63000.00), (60, 60, 49000.00),
(61, 61, 58000.00), (62, 62, 39000.00), (63, 63, 72000.00), (64, 64, 52000.00), (65, 65, 66000.00),
(66, 66, 43000.00), (67, 67, 30000.00), (68, 68, 88000.00), (69, 69, 95000.00), (70, 70, 51000.00),
(71, 71, 1200000.00), (72, 72, 110000.00), (73, 73, 80000.00), (74, 74, 5000000.00), (75, 75, 4000000.00),
(76, 76, 600000.00), (77, 77, 550000.00), (78, 78, 500000.00), (79, 79, 100000.00), (80, 80, 80000.00),
(81, 81, 300000.00), (82, 82, 400000.00), (83, 83, 99000.00), (84, 84, 150000.00), (85, 85, 75000.00),
(86, 86, 85000.00), (87, 87, 950000.00), (88, 88, 750000.00), (89, 89, 850000.00), (90, 90, 650000.00),
(91, 91, 600000.00), (92, 92, 700000.00), (93, 93, 400000.00), (94, 94, 1500000.00), (95, 95, 300000.00),
(96, 96, 800000.00), (97, 97, 900000.00), (98, 98, 120000.00), (99, 99, 20000000.00), (100, 100, 1500000.00);

-- #####################################################################
-- 6. Simulación de Transacciones, Pedidos y Detalles de Pedido
-- #####################################################################

-- VENTAs Originales (11 transacciones)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (1, 'VENTA', '2023-10-01 10:00:00', 910.00, 6, 4),(2, 'VENTA', '2023-10-01 11:30:00', 1800.50, 7, 1),(3, 'VENTA', '2023-10-02 09:15:00', 480.00, 8, 5),(4, 'VENTA', '2023-10-02 17:00:00', 1201.50, 9, 3),(5, 'VENTA', '2023-10-02 17:05:00', 3500.00, 9, 2),(6, 'VENTA', '2023-10-03 12:00:00', 3180.00, 10, 6),(7, 'VENTA', '2023-10-03 18:30:00', 630.00, 11, 4),(8, 'VENTA', '2023-10-04 11:00:00', 2060.00, 14, 3),(9, 'VENTA', '2023-10-05 19:00:00', 2500.00, 6, 1),(10, 'VENTA', '2023-10-06 10:20:00', 2852.00, 13, 3),(11, 'VENTA', '2023-10-07 08:30:00', 1360.00, 15, 5);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (1, 1, 4),(2, 2, 1),(3, 3, 5),(4, 4, 3),(5, 5, 2),(6, 6, 6),(7, 7, 4),(8, 8, 3),(9, 9, 1),(10, 10, 3),(11, 11, 5);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (1, 1, 1, 2, 600.00),(2, 1, 5, 1, 150.00),(3, 1, 6, 1, 120.00),(4, 2, 7, 1, 1800.50),(5, 3, 14, 6, 480.00),(6, 4, 19, 3, 751.50),(7, 4, 17, 1, 450.00),(8, 5, 9, 1, 3500.00),(9, 6, 10, 1, 2200.00),(10, 6, 11, 2, 700.00),(11, 6, 12, 1, 280.00),(12, 7, 4, 1, 180.00),(13, 7, 2, 1, 250.00),(14, 7, 3, 1, 200.00),(15, 8, 20, 1, 1200.00),(16, 8, 15, 1, 500.00),(17, 8, 16, 2, 360.00),(18, 9, 8, 1, 2500.00),(19, 10, 18, 5, 1400.00),(20, 10, 19, 4, 1002.00),(21, 10, 17, 1, 450.00),(22, 11, 13, 2, 400.00),(23, 11, 14, 12, 960.00);

-- VENTA 12: Homero (16) compra en la Taberna de Moe (8)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (12, 'VENTA', '2024-01-10 20:00:00', 5000.00, 16, 17);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (12, 12, 8);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (24, 12, 35, 2, 5000.00);

-- VENTA 13: Bart (22) compra en Kwik-E-Mart (7)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (13, 'VENTA', '2024-01-11 15:30:00', 750.00, 22, 18);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (13, 13, 7);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (25, 13, 39, 1, 750.00);

-- VENTA 14: Lionel Messi (31) compra en Parrilla "El Diez" (9)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (14, 'VENTA', '2024-01-12 21:00:00', 21400.00, 31, 19);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (14, 14, 9);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (26, 14, 26, 2, 7600.00), (27, 14, 27, 2, 8400.00), (28, 14, 30, 1, 4500.00), (29, 14, 28, 2, 1000.00);

-- VENTA 15: Mirtha Legrand (20) compra en su propio almacén (10) para un evento
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (15, 'EGRESO', '2024-01-13 11:00:00', 7450.00, 20, 20);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (15, 15, 10);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (30, 15, 24, 5, 4750.00), (31, 15, 25, 10, 3500.00);

-- VENTA 16: Barney (26) en la Taberna de Moe (8)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (16, 'VENTA', '2024-01-14 23:00:00', 7500.00, 26, 17);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (16, 16, 8);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (32, 16, 35, 3, 7500.00);

-- VENTA 17: Krusty (27) en Hamburguesas Krusty (11)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (17, 'VENTA', '2024-01-15 13:00:00', 3500.00, 27, 27);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (17, 17, 11);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (33, 17, 36, 2, 3000.00), (34, 17, 37, 1, 500.00);

-- VENTA 18: Ned Flanders (24) compra en Verdulería (4)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (18, 'VENTA', '2024-01-16 09:00:00', 570.00, 24, 4);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (18, 18, 4);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (35, 18, 4, 1, 180.00), (36, 18, 5, 1, 150.00), (37, 18, 6, 2, 240.00);

-- VENTA 19: Charly García (39) compra en la Taberna de Moe (8)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (19, 'VENTA', '2024-01-17 02:00:00', 3800.00, 39, 17);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (19, 19, 8);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (38, 19, 21, 1, 3000.00), (39, 19, 31, 1, 800.00);

-- VENTA 20: Marge Simpson (21) compra para la casa
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (20, 'VENTA', '2024-01-18 16:00:00', 5030.50, 21, 1);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (20, 20, 1);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (40, 20, 7, 2, 3601.00);
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (21, 'VENTA', '2024-01-18 16:10:00', 810.00, 21, 4);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (21, 21, 4);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (41, 21, 1, 1, 300.00), (42, 21, 2, 1, 250.00), (43, 21, 4, 2, 360.00);

-- VENTA 22: CLIENTE (41) compra en Puesto (3)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (22, 'VENTA', '2024-02-01 10:00:00', 1730.50, 41, 3);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (22, 22, 3);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (44, 22, 18, 2, 560.00), (45, 22, 19, 2, 501.00), (46, 22, 17, 1, 450.00);

-- VENTA 23: CLIENTE (55) compra en Puesto (9)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (23, 'VENTA', '2024-02-01 12:30:00', 5150.00, 55, 19);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (23, 23, 9);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (47, 23, 28, 5, 2500.00), (48, 23, 29, 3, 1350.00), (49, 23, 31, 1, 800.00);

-- VENTA 24: CLIENTE (82) compra en Puesto (10)
INSERT INTO transacciones (transaccion_id, tipo, fecha, monto, cuenta_origen_id, cuenta_destino_id) VALUES (24, 'VENTA', '2024-02-02 18:00:00', 2150.00, 82, 20);
INSERT INTO pedidos (pedido_id, transaccion_id, puesto_id) VALUES (24, 24, 10);
INSERT INTO detalles_pedido (detalle_pedido_id, pedido_id, item_id, cantidad, precio_total) VALUES (50, 24, 23, 1, 1200.00), (51, 24, 24, 1, 950.00);
