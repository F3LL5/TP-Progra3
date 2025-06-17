drop database comercio;
create database if not exists comercio;
use comercio;

create table if not exists items (
	item_id bigint auto_increment primary key,
	nombre varchar(100) not null,
	categoria varchar(100) not null
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
	duenio_id bigint null,
	comision decimal(10,2) not null,
	foreign key(duenio_id) references entidades(entidad_id)
	on delete cascade
	on update cascade
);

create table if not exists inventario_puesto (
	inventario_id bigint auto_increment primary key,
	cantidad int not null,
	puesto_id bigint not null,
	item_id bigint not null,
	stock_min int not null,
	precio_venta decimal(10,2) not null,
	costo_adquisicion decimal(10,2) not null,
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
	puesto_id bigint,
	foreign key(transaccion_id) references transacciones(transaccion_id),
	foreign key(puesto_id) references puestos(puesto_id)
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

create table if not exists inventario_costo_historial (
    id bigInt AUTO_INCREMENT PRIMARY KEY,
    inventario_id bigInt,
    costo_anterior DECIMAL(10,2),
    costo_nuevo DECIMAL(10,2),
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    foreign key(inventario_id) references inventario_puesto(inventario_id)
);

create table historial_cambio_duenio (
    id bigInt primary key auto_increment,
    puesto_id bigInt not null,
    duenio_anterior_id bigInt,
    duenio_nuevo_id bigInt,
    fecha_cambio timestamp default current_timestamp,
    foreign key (puesto_id) references puestos(puesto_id),
    foreign key (duenio_anterior_id) references entidades(entidad_id),
    foreign key (duenio_nuevo_id) references entidades(entidad_id)
);

DELIMITER //
CREATE TRIGGER trg_inventario_costo_adquisicion_update
BEFORE UPDATE ON inventario_puesto
FOR EACH ROW
BEGIN
    IF OLD.costo_adquisicion <> NEW.costo_adquisicion THEN
        INSERT INTO inventario_costo_historial (inventario_id, costo_anterior, costo_nuevo, fecha_cambio)
        VALUES (OLD.inventario_id, OLD.costo_adquisicion, NEW.costo_adquisicion, now());
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trigger_cambio_duenio
BEFORE UPDATE ON puestos
FOR EACH ROW
BEGIN
    IF OLD.duenio_id <> NEW.duenio_id THEN
        INSERT INTO historial_cambio_duenio (
            puesto_id,
            duenio_anterior_id,
            duenio_nuevo_id,
            fecha_cambio
        ) VALUES (
            OLD.puesto_id,
            OLD.duenio_id,
            NEW.duenio_id,
            now()
        );
    END IF;
END;
//
DELIMITER ;


insert into entidades(nombre,tipo_entidad,rol,edad,dni) values 
	( "Player", "Player", "ADMIN", 19, 1 ),
	( "NPC1", "NPC", "DUENO_PUESTO", 20, 2 ),
	( "NPC1", "NPC", "DUENO_PUESTO", 20, 3 );
    
insert into puestos(nombre,duenio_id,comision) values
	( "Puesto1", 3, 0.2 );