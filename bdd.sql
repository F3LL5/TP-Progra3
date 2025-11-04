create database if not exists c5_proyecto_comercio;
use c5_proyecto_comercio;

create table if not exists usuarios(
	usuario_id bigint auto_increment primary key,
	email varchar(100) not null unique,
	contraseña varchar(100) not null 
);

create table if not exists personas(
	persona_id bigint auto_increment primary key,
	nombre varchar(100) not null,
    edad int not null,
	dni int not null unique
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
    persona_id bigint not null,
    foreign key(persona_id) references personas(persona_id)
    on delete cascade
    on update cascade
);

create table if not exists empleados(
	empleado_id bigint auto_increment primary key,
    persona_id bigint not null,
	usuario_id bigint not null,
    foreign key(persona_id) references personas(persona_id),
    foreign key(usuario_id) references usuarios(usuario_id)
    on delete cascade
    on update cascade
);

create table if not exists duenios(
	duenio_id bigint auto_increment primary key,
    persona_id bigint not null,
	usuario_id bigint not null,
    foreign key(persona_id) references personas(persona_id),
    foreign key(usuario_id) references usuarios(usuario_id)
    on delete cascade
    on update cascade
);

create table if not exists tienda (
	tienda_id bigint auto_increment primary key,
    nombre varchar(100) not null,
    direccion varchar(100) not null,
	caja decimal not null,
	persona_id bigint not null,
	foreign key(persona_id) references personas(persona_id)
);

create table if not exists productos (
	producto_id bigint auto_increment primary key,
    nombre varchar(100) not null,
    categoria varchar(100) not null
);

create table if not exists inventario(
	inventario_id bigint auto_increment primary key,
	cantidad int not null,
	producto_id bigint not null,
	stock_min int not null,
	precio_venta decimal not null,
	costo_adquisicion decimal not null,
	foreign key(producto_id) references productos(producto_id)
	on delete cascade
	on update cascade
);

create table if not exists cuenta_bancarias(
	cuenta_bancaria_id bigint auto_increment primary key,
	duenio_id bigint not null,
	saldo decimal not null
);

create table if not exists transacciones(
	transaccion_id bigint auto_increment primary key,
	tipo varchar(100) not null,
	fecha timestamp default current_timestamp,
	monto decimal not null,
	origen_id bigint,
	destino_id bigint not null
);

create table if not exists pedidos (
	pedido_id bigint auto_increment primary key,
	transaccion_id bigint,
	foreign key(transaccion_id) references transacciones(transaccion_id)
);

create table if not exists detalles_pedido (
	detalle_pedido_id bigint auto_increment primary key,
    pedido_id bigint not null,
	producto_id bigint not null,
	cantidad int not null,
	sub_total decimal not null,
	foreign key(pedido_id) references pedidos(pedido_id),
	foreign key(producto_id) references productos(producto_id)
	on delete cascade
	on update cascade
);