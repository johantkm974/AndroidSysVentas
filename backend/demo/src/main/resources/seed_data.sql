-- ==============================================================================
-- SISTEMA electronicos - SCRIPT DE BASE DE DATOS REFINDADO (MYSQL / MARIADB)
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS sistema_electronicos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_electronicos;

SET FOREIGN_KEY_CHECKS = 0;

-- ==============================================================================
-- 1. MÓDULO: USUARIOS
-- ==============================================================================

DROP TABLE IF EXISTS usuarios_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS credenciales;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE credenciales (
    id_credencial BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    ultimo_login TIMESTAMP NULL,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_credencial_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

CREATE TABLE roles (
    id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE usuarios_roles (
    id_usuario_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_rol BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ur_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol) ON DELETE CASCADE,
    UNIQUE(id_usuario, id_rol)
);

-- ==============================================================================
-- 2. MÓDULO: INVENTARIO
-- ==============================================================================

DROP TABLE IF EXISTS movimientos_inventario;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS marcas;
DROP TABLE IF EXISTS categorias;

CREATE TABLE categorias (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE marcas (
    id_marca BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE proveedores (
    id_proveedor BIGINT AUTO_INCREMENT PRIMARY KEY,
    razon_social VARCHAR(150) NOT NULL,
    ruc VARCHAR(11) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(150),
    direccion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE productos (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio_compra DECIMAL(10,2) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    stock_minimo INT DEFAULT 5,
    imagen VARCHAR(255),
    id_categoria BIGINT NOT NULL,
    id_marca BIGINT NOT NULL,
    id_proveedor BIGINT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_categoria FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria),
    CONSTRAINT fk_prod_marca FOREIGN KEY (id_marca) REFERENCES marcas(id_marca),
    CONSTRAINT fk_prod_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
);

CREATE TABLE movimientos_inventario (
    id_movimiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    tipo_movimiento ENUM('ENTRADA', 'SALIDA', 'AJUSTE') NOT NULL,
    cantidad INT NOT NULL,
    stock_anterior INT NOT NULL,
    stock_actual INT NOT NULL,
    descripcion VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mov_producto FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    CONSTRAINT fk_mov_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ==============================================================================
-- 3. MÓDULO: PEDIDOS Y ENVÍOS (Reordenado para respetar dependencias de Ventas)
-- ==============================================================================

DROP TABLE IF EXISTS seguimiento_envio;
DROP TABLE IF EXISTS envios;
DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS estados_envio;
DROP TABLE IF EXISTS estados_pedido;

CREATE TABLE estados_pedido (
    id_estado_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE pedidos (
    id_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(30) UNIQUE NOT NULL,
    id_usuario_cliente BIGINT NOT NULL,
    id_estado_pedido BIGINT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    observacion VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_usuario_cliente) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_pedido_estado FOREIGN KEY (id_estado_pedido) REFERENCES estados_pedido(id_estado_pedido)
);

CREATE TABLE detalle_pedido (
    id_detalle_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_det_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    CONSTRAINT fk_det_pedido_prod FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE estados_envio (
    id_estado_envio BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE envios (
    id_envio BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    distrito VARCHAR(100) NOT NULL,
    referencia VARCHAR(255),
    id_estado_envio BIGINT NOT NULL,
    id_usuario_repartidor BIGINT,
    fecha_envio TIMESTAMP NULL,
    fecha_entrega TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_envio_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    CONSTRAINT fk_envio_estado FOREIGN KEY (id_estado_envio) REFERENCES estados_envio(id_estado_envio),
    CONSTRAINT fk_envio_repartidor FOREIGN KEY (id_usuario_repartidor) REFERENCES usuarios(id_usuario)
);

CREATE TABLE seguimiento_envio (
    id_seguimiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_envio BIGINT NOT NULL,
    id_estado_envio BIGINT NOT NULL,
    observacion VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seg_envio FOREIGN KEY (id_envio) REFERENCES envios(id_envio) ON DELETE CASCADE,
    CONSTRAINT fk_seg_estado FOREIGN KEY (id_estado_envio) REFERENCES estados_envio(id_estado_envio)
);

-- ==============================================================================
-- 4. MÓDULO: VENTAS (Se movió al final por la FK hacia Pedidos)
-- ==============================================================================

DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS metodos_pago;
DROP TABLE IF EXISTS estados_venta;

CREATE TABLE metodos_pago (
    id_metodo_pago BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE estados_venta (
    id_estado_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255)
);

CREATE TABLE ventas (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_venta VARCHAR(30) UNIQUE NOT NULL,
    id_pedido BIGINT NOT NULL, -- RELACIÓN VITAL AÑADIDA
    id_usuario_cliente BIGINT NOT NULL,
    id_usuario_vendedor BIGINT,
    id_metodo_pago BIGINT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    igv DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    id_estado_venta BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_venta_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido), -- FK AÑADIDA
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_usuario_cliente) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_venta_vendedor FOREIGN KEY (id_usuario_vendedor) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_venta_metodo FOREIGN KEY (id_metodo_pago) REFERENCES metodos_pago(id_metodo_pago),
    CONSTRAINT fk_venta_estado FOREIGN KEY (id_estado_venta) REFERENCES estados_venta(id_estado_venta)
);

CREATE TABLE detalle_venta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_det_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON DELETE CASCADE,
    CONSTRAINT fk_det_venta_prod FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);
SET FOREIGN_KEY_CHECKS = 1;


-- ==============================================================================
-- SISTEMA electronicos - SCRIPT DE DATOS SEMILLA (SOLO INSERT)
-- USAR SOLO SI NO USAS DataInitializer.java
-- Hash BCrypt válido para password "123456"
-- ==============================================================================

USE sistema_electronicos;

-- Roles
INSERT IGNORE INTO roles (nombre, descripcion) VALUES
('ROLE_ADMIN', 'Administrador total del sistema'),
('ROLE_VENDEDOR', 'Encargado de registrar ventas y gestionar clientes'),
('ROLE_ALMACENERO', 'Gestión de inventario y despachos'),
('ROLE_CLIENTE', 'Cliente final que realiza compras web'),
('ROLE_REPARTIDOR', 'Encargado de realizar entregas a domicilio');

-- Métodos de Pago
INSERT IGNORE INTO metodos_pago (nombre, descripcion) VALUES
('YAPE', 'Pago mediante billetera digital Yape'),
('PLIN', 'Pago mediante billetera digital Plin'),
('TARJETA', 'Pago con tarjeta de crédito/débito'),
('EFECTIVO', 'Pago en efectivo en tienda'),
('SIMULADO', 'Pago de prueba para entorno de desarrollo');

-- Estados de Venta
INSERT IGNORE INTO estados_venta (nombre, descripcion) VALUES
('PENDIENTE', 'Venta registrada pero el pago aún no se confirma'),
('PAGADA', 'El pago ha sido procesado exitosamente'),
('ANULADA', 'La venta fue cancelada o el pago rechazado');

-- Estados de Pedido
INSERT IGNORE INTO estados_pedido (nombre, descripcion) VALUES
('PENDIENTE', 'Pedido recién creado por el cliente'),
('CONFIRMADO', 'Stock separado y validado'),
('PREPARANDO', 'El pedido se está empaquetando en almacén'),
('ENVIADO', 'El pedido ya salió a ruta de entrega'),
('ENTREGADO', 'El cliente recibió su pedido'),
('CANCELADO', 'El pedido fue cancelado por falta de stock o solicitud');

-- Estados de Envío
INSERT IGNORE INTO estados_envio (nombre, descripcion) VALUES
('PENDIENTE', 'Aún no se ha asignado a ruta'),
('EN_RUTA', 'El repartidor está en camino'),
('ENTREGADO', 'Paquete entregado con éxito'),
('CANCELADO', 'Envío fallido o cancelado');

-- Categorías
INSERT IGNORE INTO categorias (nombre, descripcion) VALUES
('Componentes de PC', 'Hardware general para armado de equipos'),
('Periféricos', 'Teclados, ratones, audífonos');

-- Marcas
INSERT IGNORE INTO marcas (nombre, descripcion) VALUES
('NVIDIA', 'Tarjetas gráficas de alto rendimiento'),
('Logitech', 'Periféricos y accesorios');

-- Proveedores
INSERT IGNORE INTO proveedores (razon_social, ruc, telefono, correo, direccion) VALUES
('Tech Distribuciones S.A.C.', '20123456789', '01-555-1234', 'ventas@techdist.pe', 'Av. Garcilaso de la Vega 1234, Lima');

-- Productos
INSERT IGNORE INTO productos (codigo, nombre, descripcion, precio_compra, precio_venta, stock, stock_minimo, id_categoria, id_marca, id_proveedor) VALUES
('GPU-NV-4060', 'NVIDIA GeForce RTX 4060', 'Tarjeta gráfica para gaming y desarrollo', 1200.00, 1500.00, 15, 3, 1, 1, 1),
('PER-LOG-G502', 'Mouse Logitech G502 Hero', 'Mouse gamer con sensor Hero 25K', 150.00, 220.00, 30, 5, 2, 2, 1);

-- Usuarios (BCrypt hash válido de "123456")
INSERT IGNORE INTO usuarios (id_usuario, nombres, apellidos, dni, telefono, direccion) VALUES
(1, 'Johan', 'Admin', '70000001', '999888777', 'Av. Desarrollo 123'),
(2, 'johan', 'joseph', '70000002', '988777666', 'Calle Las Pruebas 404'),
(3, 'Carlos', 'Repartidor', '70000003', '977666555', 'Av. Reparto 456');

INSERT IGNORE INTO credenciales (id_usuario, correo, password) VALUES
(1, 'admin@sistema.edu.pe', '$2a$10$ApfrUWJlHwi8IT5BJJZzI.eHf29b9bokRScwNnlusR0N2cwpUkZyK'),
(2, 'johan@cliente.pe', '$2a$10$ApfrUWJlHwi8IT5BJJZzI.eHf29b9bokRScwNnlusR0N2cwpUkZyK'),
(3, 'repartidor@sistema.edu.pe', '$2a$10$ApfrUWJlHwi8IT5BJJZzI.eHf29b9bokRScwNnlusR0N2cwpUkZyK');

INSERT IGNORE INTO usuarios_roles (id_usuario, id_rol) VALUES
(1, 1),
(2, 4),
(3, 5);
