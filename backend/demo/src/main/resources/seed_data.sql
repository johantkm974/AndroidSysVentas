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
('ROLE_CLIENTE', 'Cliente final que realiza compras web');

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
(2, 'johan', 'joseph', '70000002', '988777666', 'Calle Las Pruebas 404');

INSERT IGNORE INTO credenciales (id_usuario, correo, password) VALUES
(1, 'admin@sistema.edu.pe', '$2a$10$ApfrUWJlHwi8IT5BJJZzI.eHf29b9bokRScwNnlusR0N2cwpUkZyK'),
(2, 'johan@cliente.pe', '$2a$10$ApfrUWJlHwi8IT5BJJZzI.eHf29b9bokRScwNnlusR0N2cwpUkZyK');

INSERT IGNORE INTO usuarios_roles (id_usuario, id_rol) VALUES
(1, 1),
(2, 4);
