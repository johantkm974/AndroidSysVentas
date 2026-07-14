package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

// --- Auth & User ---
data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val tipo: String,
    val idUsuario: Long,
    val nombres: String,
    val correo: String,
    val roles: List<String>
)

data class RegisterRequest(
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String?,
    val direccion: String?,
    val correo: String,
    val password: String,
    val idRoles: List<Long>? = null
)

data class UsuarioResponse(
    val idUsuario: Long,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String?,
    val direccion: String?,
    val correo: String,
    val activo: Boolean,
    val roles: List<String>
)

data class UpdateUserRequest(
    val nombres: String?,
    val apellidos: String?,
    val dni: String?,
    val telefono: String?,
    val direccion: String?,
    val activo: Boolean?,
    val idRoles: List<Long>?
)

data class RoleResponse(
    val idRol: Long,
    val nombre: String,
    val descripcion: String?
)

// --- Inventory ---
data class Categoria(
    val idCategoria: Long? = null,
    val nombre: String,
    val descripcion: String? = null,
    val activo: Boolean? = true
)

data class Marca(
    val idMarca: Long? = null,
    val nombre: String,
    val descripcion: String? = null,
    val activo: Boolean? = true
)

data class Proveedor(
    val idProveedor: Long? = null,
    val razonSocial: String,
    val ruc: String,
    val telefono: String? = null,
    val correo: String? = null,
    val direccion: String? = null,
    val activo: Boolean? = true
)

data class ProductoResponse(
    val idProducto: Long,
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val precioCompra: Double,
    val precioVenta: Double,
    val stock: Int,
    val stockMinimo: Int,
    val imagen: String?,
    val categoria: String,
    val marca: String,
    val proveedor: String,
    val activo: Boolean,
    val createdAt: String?
)

data class ProductoRequest(
    val codigo: String,
    val nombre: String,
    val descripcion: String?,
    val precioCompra: Double,
    val precioVenta: Double,
    val stock: Int,
    val stockMinimo: Int,
    val imagen: String?,
    val idCategoria: Long,
    val idMarca: Long,
    val idProveedor: Long
)

// --- Orders & Sales ---
data class PedidoRequest(
    val items: List<ItemPedido>,
    val observacion: String? = null
)

data class ItemPedido(
    val idProducto: Long,
    val cantidad: Int
)

data class PedidoResponse(
    val idPedido: Long,
    val numeroPedido: String,
    val cliente: String,
    val estado: String,
    val total: Double,
    val observacion: String?,
    val createdAt: String?,
    val detalles: List<DetallePedidoResponse>?
)

data class DetallePedidoResponse(
    val idDetalle: Long,
    val producto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

data class VentaRequest(
    val idPedido: Long,
    val idMetodoPago: Long
)

data class VentaResponse(
    val idVenta: Long,
    val numeroVenta: String,
    val numeroPedido: String,
    val cliente: String,
    val metodoPago: String,
    val subtotal: Double,
    val igv: Double,
    val total: Double,
    val estado: String,
    val createdAt: String?,
    val detalles: List<DetalleVentaResponse>?
)

data class DetalleVentaResponse(
    val producto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

// --- Dashboard ---
data class DashboardResponse(
    val resumen: ResumenGeneral,
    val productosMasVendidos: List<ProductoTop>,
    val productosStockBajo: List<ProductoStockBajo>
)

data class ResumenGeneral(
    val totalProductos: Long,
    val totalUsuarios: Long,
    val totalPedidosPendientes: Long,
    val totalVentasDelMes: Long,
    val ingresosDelMes: Double,
    val totalEnviosPendientes: Long
)

data class ProductoTop(
    val idProducto: Long,
    val nombre: String,
    val cantidadVendida: Long
)

data class ProductoStockBajo(
    val idProducto: Long,
    val nombre: String,
    val codigo: String,
    val stock: Int,
    val stockMinimo: Int
)
