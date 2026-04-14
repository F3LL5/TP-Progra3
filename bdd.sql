create database if not exists proyecto_comercio;
use proyecto_comercio;


-- ---------------------------------------------------------------------------------------
-- Tablas para personas y usuarios
-- ---------------------------------------------------------------------------------------

create table if not exists usuarios(
	usuario_id bigint auto_increment primary key,
	email varchar(255) not null unique,
	contraseña varchar(255) not null,
	rol enum ('ROLE_ADMIN','ROLE_DUENIO','ROLE_EMPLEADO') not null
);

create table if not exists personas(
	persona_id bigint auto_increment primary key,
	nombre varchar(255) not null,
	apellido varchar(255) not null,
	fecha_nacimiento date not null,
	dni bigint not null unique
);

create table if not exists clientes(
	cliente_id bigint auto_increment primary key,
    persona_id bigint not null,
    foreign key(persona_id) references personas(persona_id)
    on delete cascade
    on update cascade
);

create table if not exists proveedores(
	proveedor_id bigint auto_increment primary key,
    cuit bigint(13) unique not null,
    razon_social varchar(150) not null,
    nombre_fantasia varchar(150),
    condicion_iva enum ('ResponsableInscripto','Monotributo','Exento','ConsumidorFinal') not null,
    telefono bigint,
    email varchar(100),
    -- Campos del Domicilio (@Embedded)
    dir_calle VARCHAR(255),
    dir_altura VARCHAR(255),
    dir_piso VARCHAR(255),
    dir_cp VARCHAR(255),
    dir_localidad VARCHAR(255),
    dir_provincia VARCHAR(255),
    dir_pais VARCHAR(255) DEFAULT 'Argentina'
);

create table if not exists empleados(
	empleado_id bigint auto_increment primary key,
    persona_id bigint not null,
	usuario_id bigint not null,
    foreign key(persona_id) references personas(persona_id)
	on delete cascade
    on update cascade,
    foreign key(usuario_id) references usuarios(usuario_id)
    on delete cascade
    on update cascade
);

create table if not exists duenios(
	duenio_id bigint auto_increment primary key,
  persona_id bigint not null,
	usuario_id bigint not null,
  foreign key(persona_id) references personas(persona_id)
	on delete cascade
    on update cascade,
    foreign key(usuario_id) references usuarios(usuario_id)
    on delete cascade
    on update cascade
);

create table if not exists tiendas (
	tienda_id bigint auto_increment primary key,
    nombre varchar(255) not null,
    direccion varchar(255) not null,
	caja decimal(38,2) not null,
	duenio_id bigint not null,
	foreign key(duenio_id) references duenios(duenio_id)
	ON DELETE RESTRICT
    ON UPDATE CASCADE
);


-- ---------------------------------------------------------------------------------------
-- Tablas para productos
-- ---------------------------------------------------------------------------------------

create table if not exists productos (
	producto_id bigint auto_increment primary key,
    nombre varchar(255) not null,
    categoria varchar(255) not null,
	producto_imagen varchar(255),
	UNIQUE KEY unique_producto (nombre, categoria)
);

create table if not exists inventarios(
	inventario_id bigint auto_increment primary key,
	cantidad int not null,
	producto_id bigint not null unique,
	stock_min int,
	precio_venta decimal(10,2) null,
	costo_adquisicion decimal(10,2) not null,
	foreign key(producto_id) references productos(producto_id)
	on delete cascade
	on update cascade
);

create table if not exists lotes (
	lote_id bigint not null auto_increment,
	producto_id bigint not null,
    cantidad_disponible integer not null,
    costo_unitario decimal(38,2) not null,
    fecha_ingreso date,
    primary key (lote_id),
    foreign key(producto_id) references productos(producto_id)
    on delete cascade
    on update cascade
);

-- ---------------------------------------------------------------------------------------
-- Tablas para pedidos y transacciones
-- ---------------------------------------------------------------------------------------

create table if not exists cuenta_bancarias(
	cuenta_bancaria_id bigint auto_increment primary key,
	tienda_id bigint not null,
	cbu bigint not null unique,
	saldo decimal(38,2) not null,
	foreign key(tienda_id) references tiendas(tienda_id)
	ON DELETE RESTRICT
    ON UPDATE CASCADE
);

create table if not exists transacciones(
	transaccion_id bigint auto_increment primary key,
	tipo varchar(100) not null,
	fecha datetime default current_timestamp,
	monto decimal(38,2) not null,
	origen_id bigint,
	destino_id bigint not null
);

create table if not exists pedidos (
	pedido_id bigint auto_increment primary key,
	transaccion_id bigint,
	tipo enum ('COMPRA','VENTA') not null,
	estado enum('PENDIENTE','FINALIZADO'),
	foreign key(transaccion_id) references transacciones(transaccion_id)
	ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- Tabla para los detalles de cada pedido
create table if not exists detalles_pedido (
	detalle_pedido_id bigint auto_increment primary key,
    pedido_id bigint not null,
    producto_id bigint not null,
    cantidad int not null,
    subtotal decimal(10,2) not null,
    foreign key(pedido_id) references pedidos(pedido_id)
    on delete cascade
    on update cascade,
    foreign key(producto_id) references productos(producto_id)
    on delete restrict
    on update cascade
);
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- TABLAS historial
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
create table if not exists historial_usuarios(
	historial_usuario_id bigint auto_increment primary key,
	usuario_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
	email varchar(255),
	contraseña varchar(255),
	rol enum ('ROLE_ADMIN','ROLE_DUENIO','ROLE_EMPLEADO')
);

CREATE TABLE IF NOT EXISTS historial_personas (
    historial_persona_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Datos en ese momento
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    fecha_nacimiento date,
    dni bigint,
    -- Detalle del cambio
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

create table if not exists historial_clientes(
	historial_cliente_id bigint auto_increment primary key,
	cliente_id bigint,
	persona_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp
);

CREATE TABLE IF NOT EXISTS historial_proveedores (
    historial_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id BIGINT,
    cuit_afectado BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP
);

create table if not exists historial_empleados(
	historial_empleado_id bigint auto_increment primary key,
	empleado_id bigint,
	persona_id bigint,
	usuario_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp
);

create table if not exists historial_duenios(
	historial_duenio_id bigint auto_increment primary key,
	duenio_id bigint,
	persona_id bigint,
	usuario_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp
);

create table if not exists historial_tiendas(
	historial_tienda_id bigint auto_increment primary key,
	tienda_id bigint,
	duenio_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
	nombre varchar(255),
	direccion varchar(255),
	caja decimal(38,2)
);

create table if not exists historial_productos(
	historial_producto_id bigint auto_increment primary key,
	producto_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
	nombre varchar(255),
    categoria varchar(255),
	producto_imagen varchar(255)
);

create table if not exists historial_inventarios(
	historial_inventario_id bigint auto_increment primary key,
	inventario_id bigint,
	producto_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
	cantidad int,
	stock_min int,
	precio_venta decimal(10,2),
	costo_adquisicion decimal(10,2)
);

create table if not exists historial_lotes(
	historial_lote_id bigint auto_increment primary key,
	lote_id bigint,
	producto_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
    cantidad_disponible integer,
    costo_unitario decimal(38,2),
    fecha_ingreso date
);

create table if not exists historial_cuenta_bancarias(
	historial_cuenta_bancaria_id bigint auto_increment primary key,
	cuenta_bancaria_id bigint,
	tienda_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
	cbu bigint,
	saldo decimal(38,2)
);

create table if not exists historial_transacciones(
	historial_transaccion_id bigint auto_increment primary key,
	transaccion_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
	tipo varchar(100),
	fecha datetime,
	monto decimal(38,2),
	origen_id bigint,
	destino_id bigint
);

create table if not exists historial_pedidos(
	historial_pedido_id bigint auto_increment primary key,
	pedido_id bigint,
	transaccion_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
	tipo enum ('COMPRA','VENTA'),
	estado enum('PENDIENTE','FINALIZADO')
);

create table if not exists historial_detalles_pedido(
	historial_detalle_pedido_id bigint auto_increment primary key,
	detalle_pedido_id bigint,
	pedido_id bigint,
	producto_id bigint,
	accion enum('INSERT','UPDATE','DELETE') not null,
	fecha_evento datetime default current_timestamp,
    cantidad int,
    subtotal decimal(10,2)
);

-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- TRIGGERS 
-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

DELIMITER $$

-- =========================
-- USUARIOS
-- =========================
CREATE TRIGGER trg_usuarios_ai
AFTER INSERT ON usuarios
FOR EACH ROW
BEGIN
  INSERT INTO historial_usuarios(usuario_id, accion, email, contraseña, rol)
  VALUES (NEW.usuario_id, 'INSERT', NEW.email, NEW.contraseña, NEW.rol);
END$$

CREATE TRIGGER trg_usuarios_au
AFTER UPDATE ON usuarios
FOR EACH ROW
BEGIN
  INSERT INTO historial_usuarios(usuario_id, accion, email, contraseña, rol)
  VALUES (NEW.usuario_id, 'UPDATE', NEW.email, NEW.contraseña, NEW.rol);
END$$

CREATE TRIGGER trg_usuarios_bd
BEFORE DELETE ON usuarios
FOR EACH ROW
BEGIN
  INSERT INTO historial_usuarios(usuario_id, accion, email, contraseña, rol)
  VALUES (OLD.usuario_id, 'DELETE', OLD.email, OLD.contraseña, OLD.rol);
END$$


-- =========================
-- PERSONAS
-- =========================
CREATE TRIGGER trg_personas_ai
AFTER INSERT ON personas
FOR EACH ROW
BEGIN
  INSERT INTO historial_personas(persona_id, accion, nombre, apellido, fecha_nacimiento, dni)
  VALUES (NEW.persona_id, 'INSERT', NEW.nombre, NEW.apellido, NEW.fecha_nacimiento, NEW.dni);
END$$

CREATE TRIGGER trg_personas_au
AFTER UPDATE ON personas
FOR EACH ROW
BEGIN
    -- Detectar cambio en nombre
    IF (OLD.nombre <> NEW.nombre) THEN
        INSERT INTO historial_personas(persona_id, accion, nombre, apellido, fecha_nacimiento, dni, campo_modificado, valor_anterior, valor_nuevo)
        VALUES (NEW.persona_id, 'UPDATE', NEW.nombre, NEW.apellido, NEW.fecha_nacimiento, NEW.dni, 'nombre', OLD.nombre, NEW.nombre);
    END IF;
    
    IF (OLD.apellido <> NEW.apellido) THEN
        INSERT INTO historial_personas(persona_id, accion, nombre, apellido, fecha_nacimiento, dni, campo_modificado, valor_anterior, valor_nuevo)
        VALUES (NEW.persona_id, 'UPDATE', NEW.nombre, NEW.apellido, NEW.fecha_nacimiento, NEW.dni, 'apellido', OLD.apellido, NEW.apellido);
    END IF;

    -- Detectar cambio en edad
    IF (OLD.fecha_nacimiento <> NEW.fecha_nacimiento) THEN
        INSERT INTO historial_personas(persona_id, accion, nombre, apellido, fecha_nacimiento, dni, campo_modificado, valor_anterior, valor_nuevo)
        VALUES (NEW.persona_id, 'UPDATE', NEW.nombre, NEW.apellido, NEW.fecha_nacimiento, NEW.dni, 'fecha_nacimiento', OLD.fecha_nacimiento, NEW.fecha_nacimiento);
    END IF;

    -- Detectar cambio en DNI
    IF (OLD.dni <> NEW.dni) THEN
        INSERT INTO historial_personas(persona_id, accion, nombre, apellido, fecha_nacimiento, dni, campo_modificado, valor_anterior, valor_nuevo)
        VALUES (NEW.persona_id, 'UPDATE', NEW.nombre, NEW.apellido, NEW.fecha_nacimiento, NEW.dni, 'dni', OLD.dni, NEW.dni);
    END IF;
END$$

CREATE TRIGGER trg_personas_bd
BEFORE DELETE ON personas
FOR EACH ROW
BEGIN
  INSERT INTO historial_personas(persona_id, accion, nombre, apellido, fecha_nacimiento, dni)
  VALUES (OLD.persona_id, 'DELETE', OLD.nombre, OLD.apellido, OLD.fecha_nacimiento, OLD.dni);
END$$


-- =========================
-- CLIENTES
-- =========================
CREATE TRIGGER trg_clientes_ai
AFTER INSERT ON clientes
FOR EACH ROW
BEGIN
  INSERT INTO historial_clientes(cliente_id, persona_id, accion)
  VALUES (NEW.cliente_id, NEW.persona_id, 'INSERT');
END$$

CREATE TRIGGER trg_clientes_au
AFTER UPDATE ON clientes
FOR EACH ROW
BEGIN
  INSERT INTO historial_clientes(cliente_id, persona_id, accion)
  VALUES (NEW.cliente_id, NEW.persona_id, 'UPDATE');
END$$

CREATE TRIGGER trg_clientes_bd
BEFORE DELETE ON clientes
FOR EACH ROW
BEGIN
  INSERT INTO historial_clientes(cliente_id, persona_id, accion)
  VALUES (OLD.cliente_id, OLD.persona_id, 'DELETE');
END$$


-- =========================
-- PROVEEDORES
-- =========================
CREATE TRIGGER trg_proveedores_ai
AFTER INSERT ON proveedores
FOR EACH ROW
BEGIN
  INSERT INTO historial_proveedores(proveedor_id, cuit_afectado, accion)
  VALUES (NEW.proveedor_id, NEW.cuit, 'INSERT');
END$$

CREATE TRIGGER trg_proveedores_au
AFTER UPDATE ON proveedores
FOR EACH ROW
BEGIN
  INSERT INTO historial_proveedores(proveedor_id, cuit_afectado, accion)
  VALUES (NEW.proveedor_id, NEW.cuit, 'UPDATE');
END$$

CREATE TRIGGER trg_proveedores_bd
BEFORE DELETE ON proveedores
FOR EACH ROW
BEGIN
  INSERT INTO historial_proveedores(proveedor_id, cuit_afectado, accion)
  VALUES (OLD.proveedor_id, OLD.cuit, 'DELETE');
END$$


-- =========================
-- EMPLEADOS
-- =========================
CREATE TRIGGER trg_empleados_ai
AFTER INSERT ON empleados
FOR EACH ROW
BEGIN
  INSERT INTO historial_empleados(empleado_id, persona_id, usuario_id, accion)
  VALUES (NEW.empleado_id, NEW.persona_id, NEW.usuario_id, 'INSERT');
END$$

CREATE TRIGGER trg_empleados_au
AFTER UPDATE ON empleados
FOR EACH ROW
BEGIN
  INSERT INTO historial_empleados(empleado_id, persona_id, usuario_id, accion)
  VALUES (NEW.empleado_id, NEW.persona_id, NEW.usuario_id, 'UPDATE');
END$$

CREATE TRIGGER trg_empleados_bd
BEFORE DELETE ON empleados
FOR EACH ROW
BEGIN
  INSERT INTO historial_empleados(empleado_id, persona_id, usuario_id, accion)
  VALUES (OLD.empleado_id, OLD.persona_id, OLD.usuario_id, 'DELETE');
END$$


-- =========================
-- DUENIOS
-- =========================
CREATE TRIGGER trg_duenios_ai
AFTER INSERT ON duenios
FOR EACH ROW
BEGIN
  INSERT INTO historial_duenios(duenio_id, persona_id, usuario_id, accion)
  VALUES (NEW.duenio_id, NEW.persona_id, NEW.usuario_id, 'INSERT');
END$$

CREATE TRIGGER trg_duenios_au
AFTER UPDATE ON duenios
FOR EACH ROW
BEGIN
  INSERT INTO historial_duenios(duenio_id, persona_id, usuario_id, accion)
  VALUES (NEW.duenio_id, NEW.persona_id, NEW.usuario_id, 'UPDATE');
END$$

CREATE TRIGGER trg_duenios_bd
BEFORE DELETE ON duenios
FOR EACH ROW
BEGIN
  INSERT INTO historial_duenios(duenio_id, persona_id, usuario_id, accion)
  VALUES (OLD.duenio_id, OLD.persona_id, OLD.usuario_id, 'DELETE');
END$$


-- =========================
-- TIENDAS
-- =========================
CREATE TRIGGER trg_tiendas_ai
AFTER INSERT ON tiendas
FOR EACH ROW
BEGIN
  INSERT INTO historial_tiendas(tienda_id, duenio_id, accion, nombre, direccion, caja)
  VALUES (NEW.tienda_id, NEW.duenio_id, 'INSERT', NEW.nombre, NEW.direccion, NEW.caja);
END$$

CREATE TRIGGER trg_tiendas_au
AFTER UPDATE ON tiendas
FOR EACH ROW
BEGIN
  INSERT INTO historial_tiendas(tienda_id, duenio_id, accion, nombre, direccion, caja)
  VALUES (NEW.tienda_id, NEW.duenio_id, 'UPDATE', NEW.nombre, NEW.direccion, NEW.caja);
END$$

CREATE TRIGGER trg_tiendas_bd
BEFORE DELETE ON tiendas
FOR EACH ROW
BEGIN
  INSERT INTO historial_tiendas(tienda_id, duenio_id, accion, nombre, direccion, caja)
  VALUES (OLD.tienda_id, OLD.duenio_id, 'DELETE', OLD.nombre, OLD.direccion, OLD.caja);
END$$


-- =========================
-- PRODUCTOS
-- =========================
CREATE TRIGGER trg_productos_ai
AFTER INSERT ON productos
FOR EACH ROW
BEGIN
  INSERT INTO historial_productos(producto_id, accion, nombre, categoria, producto_imagen)
  VALUES (NEW.producto_id, 'INSERT', NEW.nombre, NEW.categoria, NEW.producto_imagen);
END$$

CREATE TRIGGER trg_productos_au
AFTER UPDATE ON productos
FOR EACH ROW
BEGIN
  INSERT INTO historial_productos(producto_id, accion, nombre, categoria, producto_imagen)
  VALUES (NEW.producto_id, 'UPDATE', NEW.nombre, NEW.categoria, NEW.producto_imagen);
END$$

CREATE TRIGGER trg_productos_bd
BEFORE DELETE ON productos
FOR EACH ROW
BEGIN
  INSERT INTO historial_productos(producto_id, accion, nombre, categoria, producto_imagen)
  VALUES (OLD.producto_id, 'DELETE', OLD.nombre, OLD.categoria, OLD.producto_imagen);
END$$


-- =========================
-- INVENTARIOS
-- =========================
CREATE TRIGGER trg_inventarios_ai
AFTER INSERT ON inventarios
FOR EACH ROW
BEGIN
  INSERT INTO historial_inventarios(inventario_id, producto_id, accion, cantidad, stock_min, precio_venta, costo_adquisicion)
  VALUES (NEW.inventario_id, NEW.producto_id, 'INSERT', NEW.cantidad, NEW.stock_min, NEW.precio_venta, NEW.costo_adquisicion);
END$$

CREATE TRIGGER trg_inventarios_au
AFTER UPDATE ON inventarios
FOR EACH ROW
BEGIN
  INSERT INTO historial_inventarios(inventario_id, producto_id, accion, cantidad, stock_min, precio_venta, costo_adquisicion)
  VALUES (NEW.inventario_id, NEW.producto_id, 'UPDATE', NEW.cantidad, NEW.stock_min, NEW.precio_venta, NEW.costo_adquisicion);
END$$

CREATE TRIGGER trg_inventarios_bd
BEFORE DELETE ON inventarios
FOR EACH ROW
BEGIN
  INSERT INTO historial_inventarios(inventario_id, producto_id, accion, cantidad, stock_min, precio_venta, costo_adquisicion)
  VALUES (OLD.inventario_id, OLD.producto_id, 'DELETE', OLD.cantidad, OLD.stock_min, OLD.precio_venta, OLD.costo_adquisicion);
END$$


-- =========================
-- LOTES
-- =========================
CREATE TRIGGER trg_lotes_ai
AFTER INSERT ON lotes
FOR EACH ROW
BEGIN
  INSERT INTO historial_lotes(lote_id, producto_id, accion, cantidad_disponible, costo_unitario, fecha_ingreso)
  VALUES (NEW.lote_id, NEW.producto_id, 'INSERT', NEW.cantidad_disponible, NEW.costo_unitario, NEW.fecha_ingreso);
END$$

CREATE TRIGGER trg_lotes_au
AFTER UPDATE ON lotes
FOR EACH ROW
BEGIN
  INSERT INTO historial_lotes(lote_id, producto_id, accion, cantidad_disponible, costo_unitario, fecha_ingreso)
  VALUES (NEW.lote_id, NEW.producto_id, 'UPDATE', NEW.cantidad_disponible, NEW.costo_unitario, NEW.fecha_ingreso);
END$$

CREATE TRIGGER trg_lotes_bd
BEFORE DELETE ON lotes
FOR EACH ROW
BEGIN
  INSERT INTO historial_lotes(lote_id, producto_id, accion, cantidad_disponible, costo_unitario, fecha_ingreso)
  VALUES (OLD.lote_id, OLD.producto_id, 'DELETE', OLD.cantidad_disponible, OLD.costo_unitario, OLD.fecha_ingreso);
END$$


-- =========================
-- CUENTA_BANCARIAS
-- =========================
CREATE TRIGGER trg_cuenta_bancarias_ai
AFTER INSERT ON cuenta_bancarias
FOR EACH ROW
BEGIN
  INSERT INTO historial_cuenta_bancarias(cuenta_bancaria_id, tienda_id, accion, cbu, saldo)
  VALUES (NEW.cuenta_bancaria_id, NEW.tienda_id, 'INSERT', NEW.cbu, NEW.saldo);
END$$

CREATE TRIGGER trg_cuenta_bancarias_au
AFTER UPDATE ON cuenta_bancarias
FOR EACH ROW
BEGIN
  INSERT INTO historial_cuenta_bancarias(cuenta_bancaria_id, tienda_id, accion, cbu, saldo)
  VALUES (NEW.cuenta_bancaria_id, NEW.tienda_id, 'UPDATE', NEW.cbu, NEW.saldo);
END$$

CREATE TRIGGER trg_cuenta_bancarias_bd
BEFORE DELETE ON cuenta_bancarias
FOR EACH ROW
BEGIN
  INSERT INTO historial_cuenta_bancarias(cuenta_bancaria_id, tienda_id, accion, cbu, saldo)
  VALUES (OLD.cuenta_bancaria_id, OLD.tienda_id, 'DELETE', OLD.cbu, OLD.saldo);
END$$


-- =========================
-- TRANSACCIONES
-- =========================
CREATE TRIGGER trg_transacciones_ai
AFTER INSERT ON transacciones
FOR EACH ROW
BEGIN
  INSERT INTO historial_transacciones(transaccion_id, accion, tipo, fecha, monto, origen_id, destino_id)
  VALUES (NEW.transaccion_id, 'INSERT', NEW.tipo, NEW.fecha, NEW.monto, NEW.origen_id, NEW.destino_id);
END$$

CREATE TRIGGER trg_transacciones_au
AFTER UPDATE ON transacciones
FOR EACH ROW
BEGIN
  INSERT INTO historial_transacciones(transaccion_id, accion, tipo, fecha, monto, origen_id, destino_id)
  VALUES (NEW.transaccion_id, 'UPDATE', NEW.tipo, NEW.fecha, NEW.monto, NEW.origen_id, NEW.destino_id);
END$$

CREATE TRIGGER trg_transacciones_bd
BEFORE DELETE ON transacciones
FOR EACH ROW
BEGIN
  INSERT INTO historial_transacciones(transaccion_id, accion, tipo, fecha, monto, origen_id, destino_id)
  VALUES (OLD.transaccion_id, 'DELETE', OLD.tipo, OLD.fecha, OLD.monto, OLD.origen_id, OLD.destino_id);
END$$


-- =========================
-- PEDIDOS
-- =========================
CREATE TRIGGER trg_pedidos_ai
AFTER INSERT ON pedidos
FOR EACH ROW
BEGIN
  INSERT INTO historial_pedidos(pedido_id, transaccion_id, accion, tipo, estado)
  VALUES (NEW.pedido_id, NEW.transaccion_id, 'INSERT', NEW.tipo, NEW.estado);
END$$

CREATE TRIGGER trg_pedidos_au
AFTER UPDATE ON pedidos
FOR EACH ROW
BEGIN
  INSERT INTO historial_pedidos(pedido_id, transaccion_id, accion, tipo, estado)
  VALUES (NEW.pedido_id, NEW.transaccion_id, 'UPDATE', NEW.tipo, NEW.estado);
END$$

CREATE TRIGGER trg_pedidos_bd
BEFORE DELETE ON pedidos
FOR EACH ROW
BEGIN
  INSERT INTO historial_pedidos(pedido_id, transaccion_id, accion, tipo, estado)
  VALUES (OLD.pedido_id, OLD.transaccion_id, 'DELETE', OLD.tipo, OLD.estado);
END$$


-- =========================
-- DETALLES_PEDIDO
-- =========================
CREATE TRIGGER trg_detalles_pedido_ai
AFTER INSERT ON detalles_pedido
FOR EACH ROW
BEGIN
  INSERT INTO historial_detalles_pedido(detalle_pedido_id, pedido_id, producto_id, accion, cantidad, subtotal)
  VALUES (NEW.detalle_pedido_id, NEW.pedido_id, NEW.producto_id, 'INSERT', NEW.cantidad, NEW.subtotal);
END$$

CREATE TRIGGER trg_detalles_pedido_au
AFTER UPDATE ON detalles_pedido
FOR EACH ROW
BEGIN
  INSERT INTO historial_detalles_pedido(detalle_pedido_id, pedido_id, producto_id, accion, cantidad, subtotal)
  VALUES (NEW.detalle_pedido_id, NEW.pedido_id, NEW.producto_id, 'UPDATE', NEW.cantidad, NEW.subtotal);
END$$

CREATE TRIGGER trg_detalles_pedido_bd
BEFORE DELETE ON detalles_pedido
FOR EACH ROW
BEGIN
  INSERT INTO historial_detalles_pedido(detalle_pedido_id, pedido_id, producto_id, accion, cantidad, subtotal)
  VALUES (OLD.detalle_pedido_id, OLD.pedido_id, OLD.producto_id, 'DELETE', OLD.cantidad, OLD.subtotal);
END$$


DELIMITER ;
