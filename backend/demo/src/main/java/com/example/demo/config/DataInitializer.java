package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final EstadoEnvioRepository estadoEnvioRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CredencialRepository credencialRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            log.info("Base de datos ya inicializada");
            return;
        }

        log.info("Inicializando datos semilla...");

        // ===== Roles =====
        Rol admin = rolRepository.save(Rol.builder().nombre("ROLE_ADMIN").descripcion("Administrador total del sistema").build());
        Rol vendedor = rolRepository.save(Rol.builder().nombre("ROLE_VENDEDOR").descripcion("Encargado de registrar ventas y gestionar clientes").build());
        Rol almacenero = rolRepository.save(Rol.builder().nombre("ROLE_ALMACENERO").descripcion("Gestión de inventario y despachos").build());
        Rol cliente = rolRepository.save(Rol.builder().nombre("ROLE_CLIENTE").descripcion("Cliente final que realiza compras web").build());
        Rol repartidor = rolRepository.save(Rol.builder().nombre("ROLE_REPARTIDOR").descripcion("Encargado de realizar entregas a domicilio").build());

        // ===== Métodos de Pago =====
        metodoPagoRepository.save(MetodoPago.builder().nombre("YAPE").descripcion("Pago mediante billetera digital Yape").activo(true).build());
        metodoPagoRepository.save(MetodoPago.builder().nombre("PLIN").descripcion("Pago mediante billetera digital Plin").activo(true).build());
        metodoPagoRepository.save(MetodoPago.builder().nombre("TARJETA").descripcion("Pago con tarjeta de crédito/débito").activo(true).build());
        metodoPagoRepository.save(MetodoPago.builder().nombre("EFECTIVO").descripcion("Pago en efectivo en tienda").activo(true).build());
        metodoPagoRepository.save(MetodoPago.builder().nombre("SIMULADO").descripcion("Pago de prueba para entorno de desarrollo").activo(true).build());

        // ===== Estados de Venta =====
        estadoVentaRepository.save(EstadoVenta.builder().nombre("PENDIENTE").descripcion("Venta registrada pero el pago aún no se confirma").build());
        estadoVentaRepository.save(EstadoVenta.builder().nombre("PAGADA").descripcion("El pago ha sido procesado exitosamente").build());
        estadoVentaRepository.save(EstadoVenta.builder().nombre("ANULADA").descripcion("La venta fue cancelada o el pago rechazado").build());

        // ===== Estados de Pedido =====
        estadoPedidoRepository.save(EstadoPedido.builder().nombre("PENDIENTE").descripcion("Pedido recién creado por el cliente").build());
        estadoPedidoRepository.save(EstadoPedido.builder().nombre("CONFIRMADO").descripcion("Stock separado y validado").build());
        estadoPedidoRepository.save(EstadoPedido.builder().nombre("PREPARANDO").descripcion("El pedido se está empaquetando en almacén").build());
        estadoPedidoRepository.save(EstadoPedido.builder().nombre("ENVIADO").descripcion("El pedido ya salió a ruta de entrega").build());
        estadoPedidoRepository.save(EstadoPedido.builder().nombre("ENTREGADO").descripcion("El cliente recibió su pedido").build());
        estadoPedidoRepository.save(EstadoPedido.builder().nombre("CANCELADO").descripcion("El pedido fue cancelado por falta de stock o solicitud").build());

        // ===== Estados de Envío =====
        estadoEnvioRepository.save(EstadoEnvio.builder().nombre("PENDIENTE").descripcion("Aún no se ha asignado a ruta").build());
        estadoEnvioRepository.save(EstadoEnvio.builder().nombre("EN_RUTA").descripcion("El repartidor está en camino").build());
        estadoEnvioRepository.save(EstadoEnvio.builder().nombre("ENTREGADO").descripcion("Paquete entregado con éxito").build());
        estadoEnvioRepository.save(EstadoEnvio.builder().nombre("CANCELADO").descripcion("Envío fallido o cancelado").build());

        // ===== Categorías =====
        Categoria catComponentes = categoriaRepository.save(Categoria.builder().nombre("Componentes de PC").descripcion("Hardware general para armado de equipos").build());
        Categoria catPerifericos = categoriaRepository.save(Categoria.builder().nombre("Periféricos").descripcion("Teclados, ratones, audífonos").build());

        // ===== Marcas =====
        Marca marcaNvidia = marcaRepository.save(Marca.builder().nombre("NVIDIA").descripcion("Tarjetas gráficas de alto rendimiento").build());
        Marca marcaLogitech = marcaRepository.save(Marca.builder().nombre("Logitech").descripcion("Periféricos y accesorios").build());

        // ===== Proveedores =====
        Proveedor proveedor = proveedorRepository.save(Proveedor.builder()
                .razonSocial("Tech Distribuciones S.A.C.").ruc("20123456789")
                .telefono("01-555-1234").correo("ventas@techdist.pe")
                .direccion("Av. Garcilaso de la Vega 1234, Lima").build());

        // ===== Productos =====
        productoRepository.save(Producto.builder()
                .codigo("GPU-NV-4060").nombre("NVIDIA GeForce RTX 4060")
                .descripcion("Tarjeta gráfica para gaming y desarrollo")
                .precioCompra(new java.math.BigDecimal("1200.00"))
                .precioVenta(new java.math.BigDecimal("1500.00"))
                .stock(15).stockMinimo(3)
                .categoria(catComponentes).marca(marcaNvidia).proveedor(proveedor).build());

        productoRepository.save(Producto.builder()
                .codigo("PER-LOG-G502").nombre("Mouse Logitech G502 Hero")
                .descripcion("Mouse gamer con sensor Hero 25K")
                .precioCompra(new java.math.BigDecimal("150.00"))
                .precioVenta(new java.math.BigDecimal("220.00"))
                .stock(30).stockMinimo(5)
                .categoria(catPerifericos).marca(marcaLogitech).proveedor(proveedor).build());

        // ===== Usuarios =====
        String password = passwordEncoder.encode("123456");

        Usuario usuarioAdmin = usuarioRepository.save(Usuario.builder()
                .nombres("Johan").apellidos("Admin").dni("70000001")
                .telefono("999888777").direccion("Av. Desarrollo 123").build());

        credencialRepository.save(Credencial.builder()
                .usuario(usuarioAdmin).correo("admin@sistema.edu.pe")
                .password(password).build());

        usuarioRolRepository.save(UsuarioRol.builder().usuario(usuarioAdmin).rol(admin).build());

        Usuario usuarioCliente = usuarioRepository.save(Usuario.builder()
                .nombres("johan").apellidos("joseph").dni("70000002")
                .telefono("988777666").direccion("Calle Las Pruebas 404").build());

        credencialRepository.save(Credencial.builder()
                .usuario(usuarioCliente).correo("johan@cliente.pe")
                .password(password).build());

        usuarioRolRepository.save(UsuarioRol.builder().usuario(usuarioCliente).rol(cliente).build());

        Usuario usuarioRepartidor = usuarioRepository.save(Usuario.builder()
                .nombres("Carlos").apellidos("Repartidor").dni("70000003")
                .telefono("977666555").direccion("Av. Reparto 456").build());

        credencialRepository.save(Credencial.builder()
                .usuario(usuarioRepartidor).correo("repartidor@sistema.edu.pe")
                .password(password).build());

        usuarioRolRepository.save(UsuarioRol.builder().usuario(usuarioRepartidor).rol(repartidor).build());

        log.info("Datos semilla inicializados correctamente");
        log.info("  Admin: admin@sistema.edu.pe / 123456");
        log.info("  Cliente: johan@cliente.pe / 123456");
        log.info("  Repartidor: repartidor@sistema.edu.pe / 123456");
    }
}
