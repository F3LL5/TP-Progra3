use proyecto_comercio;

-- =======================================================================================
-- 1. TABLAS DE HISTORIAL
-- =======================================================================================

-- =========================
-- USUARIOS
-- =========================
CREATE TABLE historial_usuarios (
    historial_usuario_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    email VARCHAR(255),
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- PERSONAS
-- =========================
CREATE TABLE historial_personas (
    historial_persona_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- CLIENTES
-- =========================
CREATE TABLE historial_clientes (
    historial_cliente_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    persona_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    nombre varchar(255),
    apellido varchar(255),
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- PROVEEDORES
-- =========================
CREATE TABLE historial_proveedores (
    historial_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id BIGINT,
    razon_social VARCHAR(150),
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- EMPLEADOS
-- =========================
CREATE TABLE historial_empleados (
    historial_empleado_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- DUENIOS
-- =========================
CREATE TABLE historial_duenios (
    historial_duenio_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    duenio_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- TIENDAS
-- =========================
CREATE TABLE historial_tiendas (
    historial_tienda_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tienda_id BIGINT,
    nombre_fantasia varchar(255),
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- PRODUCTOS
-- =========================
CREATE TABLE historial_productos (
    historial_producto_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT,
    nombre VARCHAR(255),
    categoria VARCHAR(255),
    producto_imagen VARCHAR(255),
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- INVENTARIOS
-- =========================
CREATE TABLE historial_inventarios (
    historial_inventario_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventario_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- LOTES
-- =========================
CREATE TABLE historial_lotes (
    historial_lote_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lote_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- CUENTA_BANCARIAS
-- =========================
CREATE TABLE historial_cuenta_bancarias (
    historial_cuenta_bancaria_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuenta_bancaria_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- TRANSACCIONES
-- =========================
CREATE TABLE historial_transacciones (
    historial_transaccion_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaccion_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- PEDIDOS
-- =========================
CREATE TABLE historial_pedidos (
    historial_pedido_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- =========================
-- DETALLES_PEDIDO
-- =========================
CREATE TABLE historial_detalles_pedido (
    historial_detalle_pedido_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    detalle_pedido_id BIGINT,
    accion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    fecha_evento DATETIME DEFAULT CURRENT_TIMESTAMP,
    -- Detalle del cambio 
    campo_modificado VARCHAR(255),
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255)
);

-- ==============================================================================================================================================================================
-- 2. TRIGGERS (INSERT, UPDATE DETALLADO, DELETE)
-- ==============================================================================================================================================================================

DELIMITER $$

-- =========================
-- USUARIOS
-- =========================
CREATE TRIGGER trg_usuarios_ai AFTER INSERT ON usuarios FOR EACH ROW
BEGIN
  INSERT INTO historial_usuarios(usuario_id, email, accion) VALUES (NEW.usuario_id, NEW.email, 'INSERT');
END$$

CREATE TRIGGER trg_usuarios_au AFTER UPDATE ON usuarios FOR EACH ROW
BEGIN
    IF (OLD.email <> NEW.email) THEN INSERT INTO historial_usuarios(usuario_id, email, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.usuario_id, NEW.email, 'UPDATE', 'email', OLD.email, NEW.email); END IF;
    IF (OLD.rol <> NEW.rol) THEN INSERT INTO historial_usuarios(usuario_id, email, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.usuario_id, NEW.email, 'UPDATE', 'rol', OLD.rol, NEW.rol); END IF;
END$$

CREATE TRIGGER trg_usuarios_bd BEFORE DELETE ON usuarios FOR EACH ROW
BEGIN
  INSERT INTO historial_usuarios(usuario_id, email, accion) VALUES (OLD.usuario_id, OLD.email, 'DELETE');
END$$

-- =========================
-- PERSONAS
-- =========================
CREATE TRIGGER trg_personas_ai AFTER INSERT ON personas FOR EACH ROW
BEGIN
  INSERT INTO historial_personas(persona_id, nombre, apellido, accion) VALUES (NEW.persona_id, NEW.nombre, NEW.apellido, 'INSERT');
END$$

CREATE TRIGGER trg_personas_au AFTER UPDATE ON personas FOR EACH ROW
BEGIN
    IF (OLD.nombre <> NEW.nombre) THEN INSERT INTO historial_personas(persona_id, nombre, apellido, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.persona_id, NEW.nombre, NEW.apellido, 'UPDATE', 'nombre', OLD.nombre, NEW.nombre); END IF;
    IF (OLD.apellido <> NEW.apellido) THEN INSERT INTO historial_personas(persona_id, nombre, apellido, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.persona_id, NEW.nombre, NEW.apellido, 'UPDATE', 'apellido', OLD.apellido, NEW.apellido); END IF;
    IF (OLD.dni <> NEW.dni) THEN INSERT INTO historial_personas(persona_id, nombre, apellido, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.persona_id, NEW.nombre, NEW.apellido, 'UPDATE', 'dni', OLD.dni, NEW.dni); END IF;
    IF (OLD.fecha_nacimiento <> NEW.fecha_nacimiento) THEN INSERT INTO historial_personas(persona_id, nombre, apellido, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.persona_id, NEW.nombre, NEW.apellido, 'UPDATE', 'fecha_nacimiento', OLD.fecha_nacimiento, NEW.fecha_nacimiento); END IF;
END$$

CREATE TRIGGER trg_personas_bd BEFORE DELETE ON personas FOR EACH ROW
BEGIN
  INSERT INTO historial_personas(persona_id, nombre, apellido, accion) VALUES (OLD.persona_id, OLD.nombre, OLD.apellido, 'DELETE');
END$$

-- =========================
-- PROVEEDORES
-- =========================
CREATE TRIGGER trg_proveedores_ai AFTER INSERT ON proveedores FOR EACH ROW
BEGIN
  INSERT INTO historial_proveedores(proveedor_id, razon_social, accion) VALUES (NEW.proveedor_id, NEW.razon_social, 'INSERT');
END$$

CREATE TRIGGER trg_proveedores_au AFTER UPDATE ON proveedores FOR EACH ROW
BEGIN
    IF (OLD.cuit <> NEW.cuit) THEN INSERT INTO historial_proveedores(proveedor_id, razon_social, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.proveedor_id, NEW.razon_social, 'UPDATE', 'cuit', OLD.cuit, NEW.cuit); END IF;
    IF (OLD.razon_social <> NEW.razon_social) THEN INSERT INTO historial_proveedores(proveedor_id, razon_social, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.proveedor_id, NEW.razon_social, 'UPDATE', 'razon_social', OLD.razon_social, NEW.razon_social); END IF;
    IF (OLD.condicion_iva <> NEW.condicion_iva) THEN INSERT INTO historial_proveedores(proveedor_id, razon_social, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.proveedor_id, NEW.razon_social, 'UPDATE', 'condicion_iva', OLD.condicion_iva, NEW.condicion_iva); END IF;
    IF (OLD.ingresos_brutos <> NEW.ingresos_brutos) THEN INSERT INTO historial_proveedores(proveedor_id, razon_social, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.proveedor_id, NEW.razon_social, 'UPDATE', 'ingresos_brutos', OLD.ingresos_brutos, NEW.ingresos_brutos); END IF;
    IF (OLD.fecha_inicio_actividades <> NEW.fecha_inicio_actividades) THEN INSERT INTO historial_proveedores(proveedor_id, razon_social, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.proveedor_id, NEW.razon_social, 'UPDATE', 'fecha_inicio_actividades', OLD.fecha_inicio_actividades, NEW.fecha_inicio_actividades); END IF;
END$$

CREATE TRIGGER trg_proveedores_bd BEFORE DELETE ON proveedores FOR EACH ROW
BEGIN
  INSERT INTO historial_proveedores(proveedor_id, razon_social, accion) VALUES (OLD.proveedor_id, OLD.razon_social, 'DELETE');
END$$

-- =========================
-- PRODUCTOS
-- =========================
CREATE TRIGGER trg_productos_ai AFTER INSERT ON productos FOR EACH ROW
BEGIN
  INSERT INTO historial_productos(producto_id, nombre, categoria, producto_imagen, accion) VALUES (NEW.producto_id, NEW.nombre, NEW.categoria, NEW.producto_imagen, 'INSERT');
END$$

CREATE TRIGGER trg_productos_au AFTER UPDATE ON productos FOR EACH ROW
BEGIN
    IF (OLD.nombre <> NEW.nombre) THEN INSERT INTO historial_productos(producto_id, nombre, categoria, producto_imagen, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.producto_id, NEW.nombre, NEW.categoria, NEW.producto_imagen, 'UPDATE', 'nombre', OLD.nombre, NEW.nombre); END IF;
    IF (OLD.categoria <> NEW.categoria) THEN INSERT INTO historial_productos(producto_id, nombre, categoria, producto_imagen, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.producto_id, NEW.nombre, NEW.categoria, NEW.producto_imagen, 'UPDATE', 'categoria', OLD.categoria, NEW.categoria); END IF;
    IF (OLD.producto_imagen <> NEW.producto_imagen) THEN INSERT INTO historial_productos(producto_id, nombre, categoria, producto_imagen, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.producto_id, NEW.nombre, NEW.categoria, NEW.producto_imagen, 'UPDATE', 'producto_imagen', OLD.producto_imagen, NEW.producto_imagen); END IF;
END$$

CREATE TRIGGER trg_productos_bd BEFORE DELETE ON productos FOR EACH ROW
BEGIN
  INSERT INTO historial_productos(producto_id, nombre, categoria, producto_imagen, accion) VALUES (OLD.producto_id, OLD.nombre, OLD.categoria, OLD.producto_imagen, 'DELETE');
END$$

-- =========================
-- INVENTARIOS
-- =========================
CREATE TRIGGER trg_inventarios_ai AFTER INSERT ON inventarios FOR EACH ROW
BEGIN
  INSERT INTO historial_inventarios(inventario_id, accion) VALUES (NEW.inventario_id, 'INSERT');
END$$

CREATE TRIGGER trg_inventarios_au AFTER UPDATE ON inventarios FOR EACH ROW
BEGIN
    IF (OLD.cantidad <> NEW.cantidad) THEN INSERT INTO historial_inventarios(inventario_id, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.inventario_id, 'UPDATE', 'cantidad', OLD.cantidad, NEW.cantidad); END IF;
    IF (OLD.stock_min <> NEW.stock_min) THEN INSERT INTO historial_inventarios(inventario_id, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.inventario_id, 'UPDATE', 'stock_min', OLD.stock_min, NEW.stock_min); END IF;
    IF (OLD.precio_venta <> NEW.precio_venta) THEN INSERT INTO historial_inventarios(inventario_id, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.inventario_id, 'UPDATE', 'precio_venta', OLD.precio_venta, NEW.precio_venta); END IF;
    IF (OLD.costo_adquisicion <> NEW.costo_adquisicion) THEN INSERT INTO historial_inventarios(inventario_id, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.inventario_id, 'UPDATE', 'costo_adquisicion', OLD.costo_adquisicion, NEW.costo_adquisicion); END IF;
END$$

CREATE TRIGGER trg_inventarios_bd BEFORE DELETE ON inventarios FOR EACH ROW
BEGIN
  INSERT INTO historial_inventarios(inventario_id, accion) VALUES (OLD.inventario_id, 'DELETE');
END$$

-- =========================
-- TIENDAS
-- =========================
CREATE TRIGGER trg_tiendas_ai AFTER INSERT ON tiendas FOR EACH ROW
BEGIN
    INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion) VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'INSERT');
END$$

CREATE TRIGGER trg_tiendas_au AFTER UPDATE ON tiendas FOR EACH ROW
BEGIN
    -- Razon Social
    IF (OLD.razon_social <> NEW.razon_social) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'razon_social', OLD.razon_social, NEW.razon_social); 
    END IF;

    -- Nombre Fantasía
    IF (OLD.nombre_fantasia <> NEW.nombre_fantasia) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'nombre_fantasia', OLD.nombre_fantasia, NEW.nombre_fantasia); 
    END IF;

    -- CUIT
    IF (OLD.cuit <> NEW.cuit) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'cuit', CAST(OLD.cuit AS CHAR), CAST(NEW.cuit AS CHAR)); 
    END IF;

    -- Condición IVA
    IF (OLD.condicion_iva <> NEW.condicion_iva) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'condicion_iva', OLD.condicion_iva, NEW.condicion_iva); 
    END IF;

        -- URL Imagen de la Tienda
    IF (OLD.tienda_imagen <> NEW.tienda_imagen) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'tienda_imagen', OLD.tienda_imagen, NEW.tienda_imagen); 
    END IF;

    -- Ingresos Brutos
    IF (OLD.ingresos_brutos <> NEW.ingresos_brutos) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'ingresos_brutos', OLD.ingresos_brutos, NEW.ingresos_brutos); 
    END IF;

    -- Punto de Venta
    IF (OLD.punto_de_venta <> NEW.punto_de_venta) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'punto_de_venta', CAST(OLD.punto_de_venta AS CHAR), CAST(NEW.punto_de_venta AS CHAR)); 
    END IF;

    -- Saldo Caja (Usamos COALESCE por si el saldo inicial era NULL)
    IF (COALESCE(OLD.caja, 0) <> COALESCE(NEW.caja, 0)) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'caja', CAST(OLD.caja AS CHAR), CAST(NEW.caja AS CHAR)); 
    END IF;

    -- Dirección (Calle y Altura como ejemplo de auditoría de domicilio)
    IF (OLD.dir_calle <> NEW.dir_calle OR OLD.dir_altura <> NEW.dir_altura) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'domicilio', CONCAT(OLD.dir_calle, ' ', OLD.dir_altura), CONCAT(NEW.dir_calle, ' ', NEW.dir_altura)); 
    END IF;

    -- CierreCaja
    IF (OLD.ultimo_cierre_caja <> NEW.ultimo_cierre_caja) THEN 
        INSERT INTO historial_tiendas(tienda_id, nombre_fantasia, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.tienda_id, NEW.nombre_fantasia, 'UPDATE', 'ultimo_cierre_caja', OLD.ultimo_cierre_caja, NEW.ultimo_cierre_caja); 
    END IF;
END$$

-- =========================
-- PEDIDOS
-- =========================
CREATE TRIGGER trg_pedidos_ai AFTER INSERT ON pedidos FOR EACH ROW
BEGIN
  INSERT INTO historial_pedidos(pedido_id, accion) VALUES (NEW.pedido_id, 'INSERT');
END$$

CREATE TRIGGER trg_pedidos_au AFTER UPDATE ON pedidos FOR EACH ROW
BEGIN
    IF (OLD.estado <> NEW.estado) THEN INSERT INTO historial_pedidos(pedido_id, accion, campo_modificado, valor_anterior, valor_nuevo) VALUES (NEW.pedido_id, 'UPDATE', 'estado', OLD.estado, NEW.estado); END IF;
END$$

-- =========================
-- CLIENTES, EMPLEADOS, DUENIOS (Relaciones)
-- =========================

CREATE TRIGGER trg_empleados_ai AFTER INSERT ON empleados FOR EACH ROW BEGIN INSERT INTO historial_empleados(empleado_id, accion) VALUES (NEW.empleado_id, 'INSERT'); END$$
CREATE TRIGGER trg_duenios_ai AFTER INSERT ON duenios FOR EACH ROW BEGIN INSERT INTO historial_duenios(duenio_id, accion) VALUES (NEW.duenio_id, 'INSERT'); END$$


-- CLIENTES
DELIMITER $$

-- Trigger para capturar MODIFICACIONES en Clientes
CREATE TRIGGER trg_clientes_au AFTER UPDATE ON clientes FOR EACH ROW
BEGIN
    -- Si cambia la persona asociada (aunque es raro en un OneToOne)
    IF (OLD.persona_id <> NEW.persona_id) THEN 
        INSERT INTO historial_clientes(cliente_id, persona_id, accion, campo_modificado, valor_anterior, valor_nuevo) 
        VALUES (NEW.cliente_id, NEW.persona_id, 'UPDATE', 'persona_id', OLD.persona_id, NEW.persona_id); 
    END IF;
END$$

-- Trigger para capturar ELIMINACIONES en Clientes
CREATE TRIGGER trg_clientes_bd BEFORE DELETE ON clientes FOR EACH ROW
BEGIN
    INSERT INTO historial_clientes(cliente_id, persona_id, accion) 
    VALUES (OLD.cliente_id, OLD.persona_id, 'DELETE');
END$$

DELIMITER ;

DELIMITER $$
CREATE TRIGGER trg_clientes_ai AFTER INSERT ON clientes FOR EACH ROW
BEGIN
    -- Declaramos variables para traer el nombre de la otra tabla
    DECLARE v_nombre VARCHAR(255);
    DECLARE v_apellido VARCHAR(255);

    SELECT nombre, apellido INTO v_nombre, v_apellido FROM personas WHERE persona_id = NEW.persona_id;

    INSERT INTO historial_clientes(cliente_id, persona_id, nombre, apellido, accion) 
    VALUES (NEW.cliente_id, NEW.persona_id, v_nombre, v_apellido, 'INSERT');
END$$
DELIMITER ;


DELIMITER ;