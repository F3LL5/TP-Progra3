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
    cuit bigint unique not null,
    razon_social varchar(150) not null,
    nombre_fantasia varchar(150),
    condicion_iva enum ('ResponsableInscripto','Monotributo','Exento','ConsumidorFinal','NoAlcanzado') not null,
    ingresos_brutos varchar(255) default 'Exento',
    fecha_inicio_actividades date default (current_date),
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
    razon_social varchar(150) not null,
    nombre_fantasia varchar(150) not null,
	cuit bigint not null,
	condicion_iva enum('ResponsableInscripto','Monotributo','Exento','ConsumidorFinal','NoAlcanzado') not null,
    ingresos_brutos varchar(255) default 'Exento',
    fecha_inicio_actividades date default (current_date),
    punto_de_venta bigint default 1,
    caja decimal(38,2),
	tienda_imagen varchar(255),
    -- Campos del Domicilio (@Embedded)
    dir_calle VARCHAR(255),
    dir_altura VARCHAR(255),
    dir_piso VARCHAR(255),
    dir_cp VARCHAR(255),
    dir_localidad VARCHAR(255),
    dir_provincia VARCHAR(255),
    dir_pais VARCHAR(255) DEFAULT 'Argentina',
    -- Duenio
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
    nombre_banco varchar(100) not null,
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