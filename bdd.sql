drop database comercio;
create database if not exists comercio;
use comercio;

create table if not exists items (
	item_id bigint auto_increment primary key,
	nombre varchar(100) not null,
	categoria varchar(100) not null,
	costo decimal(10,2) not null
);

create table entidades(
	entidad_id bigint auto_increment primary key,
	nombre varchar(100),
	tipo_entidad varchar(100) not null,
	rol varchar(100) not null,
    edad int not null,
	dni int not null unique
);

create table if not exists puestos(
	puesto_id bigint auto_increment primary key,
	nombre varchar(100) not null,
	duenio_id bigint not null,
	foreign key(duenio_id) references entidades(entidad_id)
	on delete cascade
	on update cascade
);

create table if not exists inventario_puesto (
	inventario_id bigint auto_increment primary key,
	cantidad int not null,
	puesto_id bigint,
	item_id bigint,
	stock_min int not null,
	precio_venta decimal(10,2) not null,
	foreign key(puesto_id) references puestos(puesto_id),
	foreign key(item_id) references items(item_id)
	on delete cascade
	on update cascade
);

create table if not exists cuenta_bancaria(
	cuenta_bancaria_id bigint auto_increment primary key,
	entidad_id bigint,
	saldo decimal (10,2) not null,
	foreign key(entidad_id) references entidades(entidad_id)
);

create table if not exists transacciones(
	transaccion_id bigint auto_increment primary key,
	tipo varchar(100) not null,
	fecha timestamp default current_timestamp,
	monto decimal(10,2) not null,
	cuenta_origen_id bigint,
	cuenta_destino_id bigint,
	foreign key(cuenta_origen_id) references cuenta_bancaria(cuenta_bancaria_id),
	foreign key(cuenta_destino_id) references cuenta_bancaria(cuenta_bancaria_id)
);

create table if not exists pedidos (
	pedido_id bigint auto_increment primary key,
	transaccion_id bigint,
	foreign key(transaccion_id) references transacciones(transaccion_id)
);

create table if not exists detalles_pedido (
	detalle_pedido_id bigint auto_increment primary key,
    pedido_id bigint not null,
	item_id bigint not null,
	cantidad int not null,
	precio_total decimal(10,2) not null,
	foreign key(pedido_id) references pedidos(pedido_id),
	foreign key(item_id) references items(item_id)
	on delete cascade
	on update cascade
);

