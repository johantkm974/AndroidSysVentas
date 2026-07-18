package com.example.myapplication.network

import com.example.myapplication.model.*
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): LoginResponse
    
    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): UsuarioResponse
    
    // Usuarios
    @GET("usuarios")
    suspend fun listUsers(): List<UsuarioResponse>
    
    @GET("usuarios/perfil")
    suspend fun perfil(): UsuarioResponse

    @POST("usuarios")
    suspend fun createUser(@Body req: RegisterRequest): UsuarioResponse
    
    @GET("usuarios/{id}")
    suspend fun getUser(@Path("id") id: Long): UsuarioResponse
    
    @GET("usuarios/correo/{correo}")
    suspend fun getUserByEmail(@Path("correo") correo: String): UsuarioResponse

    @PUT("usuarios/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body req: UpdateUserRequest): UsuarioResponse
    
    @DELETE("usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    @GET("roles")
    suspend fun listRoles(): List<RoleResponse>
    
    // Productos
    @GET("productos")
    suspend fun listProducts(): List<ProductoResponse>
    
    @GET("productos/{id}")
    suspend fun getProduct(@Path("id") id: Long): ProductoResponse
    
    @GET("productos/categoria/{idCategoria}")
    suspend fun listProductsByCategory(@Path("idCategoria") idCategoria: Long): List<ProductoResponse>

    @GET("productos/activos")
    suspend fun listActiveProducts(): List<ProductoResponse>

    @GET("productos/stock-bajo")
    suspend fun lowStockProducts(): List<ProductoResponse>
    
    @POST("productos")
    suspend fun createProduct(@Body req: ProductoRequest): ProductoResponse
    
    @PUT("productos/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body req: ProductoRequest): ProductoResponse
    
    @DELETE("productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long)

    @DELETE("productos/{id}/permanente")
    suspend fun deleteProductPermanently(@Path("id") id: Long)
    
    // Categorías
    @GET("categorias")
    suspend fun listCategorias(): List<Categoria>
    
    @POST("categorias")
    suspend fun createCategoria(@Body req: Categoria): Categoria

    @DELETE("categorias/{id}")
    suspend fun deleteCategoria(@Path("id") id: Long)
    
    // Marcas
    @GET("marcas")
    suspend fun listMarcas(): List<Marca>
    
    @POST("marcas")
    suspend fun createMarca(@Body req: Marca): Marca

    @DELETE("marcas/{id}")
    suspend fun deleteMarca(@Path("id") id: Long)
    
    // Proveedores
    @GET("proveedores")
    suspend fun listProveedores(): List<Proveedor>
    
    @POST("proveedores")
    suspend fun createProveedor(@Body req: Proveedor): Proveedor

    @DELETE("proveedores/{id}")
    suspend fun deleteProveedor(@Path("id") id: Long)
    
    // Pedidos
    @GET("pedidos")
    suspend fun listOrders(): List<PedidoResponse>
    
    @GET("pedidos/mis-pedidos")
    suspend fun myOrders(): List<PedidoResponse>
    
    @GET("pedidos/{id}")
    suspend fun getOrder(@Path("id") id: Long): PedidoResponse
    
    @POST("pedidos")
    suspend fun createOrder(@Body req: PedidoRequest): PedidoResponse
    
    @POST("pedidos/{id}/cancelar")
    suspend fun cancelOrder(@Path("id") id: Long): PedidoResponse
    
    @PATCH("pedidos/{id}/estado/{estado}")
    suspend fun updateOrderStatus(@Path("id") id: Long, @Path("estado") estado: Long): PedidoResponse
    
    // Ventas
    @GET("ventas")
    suspend fun listSales(): List<VentaResponse>
    
    @GET("ventas/{id}")
    suspend fun getSale(@Path("id") id: Long): VentaResponse
    
    @POST("ventas/procesar")
    suspend fun processSale(@Body req: VentaRequest): VentaResponse
    
    @PATCH("ventas/{id}/anular")
    suspend fun cancelSale(@Path("id") id: Long): VentaResponse
    
    // Envíos
    @GET("envios")
    suspend fun listAllEnvios(): List<EnvioResponse>

    @GET("envios/mis-envios")
    suspend fun myDeliveries(): List<EnvioResponse>

    @GET("envios/{id}")
    suspend fun getEnvio(@Path("id") id: Long): EnvioResponse

    @GET("envios/pedido/{idPedido}")
    suspend fun getEnvioByPedido(@Path("idPedido") idPedido: Long): EnvioResponse

    @GET("envios/{id}/tracking")
    suspend fun getTracking(@Path("id") id: Long): List<SeguimientoResponse>

    @PATCH("envios/{id}/estado")
    suspend fun updateEnvioStatus(@Path("id") id: Long, @Body req: ActualizarEstadoEnvioRequest): EnvioResponse

    @PATCH("envios/{id}/asignar-repartidor/{idRepartidor}")
    suspend fun assignRepartidor(@Path("id") id: Long, @Path("idRepartidor") idRepartidor: Long): EnvioResponse

    // Dashboard
    @GET("dashboard/resumen")
    suspend fun dashboard(): DashboardResponse
}
