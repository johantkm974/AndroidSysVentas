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

    @GET("productos/stock-bajo")
    suspend fun lowStockProducts(): List<ProductoResponse>
    
    @POST("productos")
    suspend fun createProduct(@Body req: ProductoRequest): ProductoResponse
    
    @PUT("productos/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body req: ProductoRequest): ProductoResponse
    
    @DELETE("productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long)
    
    // Categorías
    @GET("categorias")
    suspend fun listCategorias(): List<Categoria>
    
    @POST("categorias")
    suspend fun createCategoria(@Body req: Categoria): Categoria
    
    // Marcas
    @GET("marcas")
    suspend fun listMarcas(): List<Marca>
    
    @POST("marcas")
    suspend fun createMarca(@Body req: Marca): Marca
    
    // Proveedores
    @GET("proveedores")
    suspend fun listProveedores(): List<Proveedor>
    
    @POST("proveedores")
    suspend fun createProveedor(@Body req: Proveedor): Proveedor
    
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
    
    // Dashboard
    @GET("dashboard/resumen")
    suspend fun dashboard(): DashboardResponse
}
