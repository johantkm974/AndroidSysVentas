package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

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
        log.info("=== Inicializando datos semilla ===");

        // ===== Roles =====
        Rol admin = findOrCreateRol("ROLE_ADMIN", "Administrador total del sistema");
        Rol vendedor = findOrCreateRol("ROLE_VENDEDOR", "Encargado de registrar ventas y gestionar clientes");
        Rol almacenero = findOrCreateRol("ROLE_ALMACENERO", "Gestión de inventario y despachos");
        Rol cliente = findOrCreateRol("ROLE_CLIENTE", "Cliente final que realiza compras web");
        Rol repartidor = findOrCreateRol("ROLE_REPARTIDOR", "Encargado de realizar entregas a domicilio");

        // ===== Métodos de Pago =====
        findOrCreateMetodoPago("YAPE", "Pago mediante billetera digital Yape");
        findOrCreateMetodoPago("PLIN", "Pago mediante billetera digital Plin");
        findOrCreateMetodoPago("TARJETA", "Pago con tarjeta de crédito/débito");
        findOrCreateMetodoPago("EFECTIVO", "Pago en efectivo en tienda");
        findOrCreateMetodoPago("SIMULADO", "Pago de prueba para entorno de desarrollo");

        // ===== Estados de Venta =====
        findOrCreateEstadoVenta("PENDIENTE", "Venta registrada pero el pago aún no se confirma");
        findOrCreateEstadoVenta("PAGADA", "El pago ha sido procesado exitosamente");
        findOrCreateEstadoVenta("ANULADA", "La venta fue cancelada o el pago rechazado");

        // ===== Estados de Pedido =====
        findOrCreateEstadoPedido("PENDIENTE", "Pedido recién creado por el cliente");
        findOrCreateEstadoPedido("CONFIRMADO", "Stock separado y validado");
        findOrCreateEstadoPedido("PREPARANDO", "El pedido se está empaquetando en almacén");
        findOrCreateEstadoPedido("ENVIADO", "El pedido ya salió a ruta de entrega");
        findOrCreateEstadoPedido("ENTREGADO", "El cliente recibió su pedido");
        findOrCreateEstadoPedido("CANCELADO", "El pedido fue cancelado por falta de stock o solicitud");

        // ===== Estados de Envío =====
        findOrCreateEstadoEnvio("PENDIENTE", "Aún no se ha asignado a ruta");
        findOrCreateEstadoEnvio("EN_RUTA", "El repartidor está en camino");
        findOrCreateEstadoEnvio("ENTREGADO", "Paquete entregado con éxito");
        findOrCreateEstadoEnvio("CANCELADO", "Envío fallido o cancelado");

        // ===== Categorías =====
        Categoria catComponentes = findOrCreateCategoria("Componentes de PC", "Hardware general para armado de equipos");
        Categoria catPerifericos = findOrCreateCategoria("Periféricos", "Teclados, ratones, audífonos");
        Categoria catMonitores = findOrCreateCategoria("Monitores", "Pantallas para gaming, diseño y oficina");
        Categoria catLaptops = findOrCreateCategoria("Laptops", "Computadoras portátiles");
        Categoria catRedes = findOrCreateCategoria("Redes", "Routers, switches, cables de red");
        Categoria catAlmacenamiento = findOrCreateCategoria("Almacenamiento", "SSD, HDD, pendrives, tarjetas SD");
        Categoria catAudio = findOrCreateCategoria("Audio y Video", "Parlantes, micrófonos, cámaras");
        Categoria catAccesorios = findOrCreateCategoria("Accesorios", "Fuentes de poder, gabinetes, enfriamiento");

        // ===== Marcas =====
        Marca marcaNvidia = findOrCreateMarca("NVIDIA", "Tarjetas gráficas de alto rendimiento");
        Marca marcaLogitech = findOrCreateMarca("Logitech", "Periféricos y accesorios");
        Marca marcaSamsung = findOrCreateMarca("Samsung", "Monitores, SSD y almacenamiento");
        Marca marcaCorsair = findOrCreateMarca("Corsair", "Memorias RAM, fuentes de poder, periféricos");
        Marca marcaAsus = findOrCreateMarca("ASUS", "Placas base, laptops, monitores ROG");
        Marca marcaHyperX = findOrCreateMarca("HyperX", "Audífonos, teclados gamer");
        Marca marcaKingston = findOrCreateMarca("Kingston", "Memorias RAM, SSD, almacenamiento");
        Marca marcaTP = findOrCreateMarca("TP-Link", "Routers, switches, adaptadores de red");
        Marca marcaIntel = findOrCreateMarca("Intel", "Procesadores Core i3, i5, i7, i9");
        Marca marcaAMD = findOrCreateMarca("AMD", "Procesadores Ryzen, tarjetas gráficas Radeon");

        // ===== Proveedores =====
        Proveedor provTechDist = findOrCreateProveedor("Tech Distribuciones S.A.C.", "20123456789",
                "01-555-1234", "ventas@techdist.pe", "Av. Garcilaso de la Vega 1234, Lima");
        Proveedor provCompuPeru = findOrCreateProveedor("CompuPeru Import S.A.C.", "20987654321",
                "01-555-5678", "ventas@compupeu.pe", "Av. Argentina 2500, Callao");
        Proveedor provRedesTotal = findOrCreateProveedor("RedesTotal S.A.C.", "20456789012",
                "01-555-9012", "ventas@redestotal.pe", "Jr. de la Unión 500, Cercado de Lima");
        Proveedor provGamerStore = findOrCreateProveedor("GamerStore Perú S.A.C.", "20321098765",
                "01-555-3456", "ventas@gamerstore.pe", "Av. Aviación 1200, San Isidro");

        // ===== Productos (30) =====
        // Componentes de PC
        findOrCreateProducto("GPU-NV-4060", "NVIDIA GeForce RTX 4060", "Tarjeta gráfica 8GB GDDR6 para gaming 1440p",
                "1200.00", "1500.00", 15, 3, null, catComponentes, marcaNvidia, provGamerStore);
        findOrCreateProducto("GPU-NV-4070", "NVIDIA GeForce RTX 4070", "Tarjeta gráfica 12GB GDDR6X para gaming 4K",
                "2200.00", "2700.00", 8, 2, null, catComponentes, marcaNvidia, provGamerStore);
        findOrCreateProducto("CPU-IN-13600K", "Intel Core i5-13600K", "Procesador 14 núcleos / 20 hilos, hasta 5.1 GHz",
                "1100.00", "1350.00", 12, 3, "https://www.compulandia.com.py/wp-content/uploads/2025/02/Procesador-Intel-Core-i5-14600K-14-Nucleos5ca064d2893d3228244537a140cc0cbabff9bf9a.jpg.png", catComponentes, marcaIntel, provTechDist);
        findOrCreateProducto("CPU-IN-14700K", "Intel Core i7-14700K", "Procesador 20 núcleos / 28 hilos, hasta 5.6 GHz",
                "1800.00", "2200.00", 6, 2, "https://smarttech.com.pe/6397-large_default/procesador-intel-core-i7-14700kf-3-4-5-6ghz-lga1700-20-nucleos-28mb-pn-bx8071514700kf.jpg", catComponentes, marcaIntel, provTechDist);
        findOrCreateProducto("CPU-AM-7800X3D", "AMD Ryzen 7 7800X3D", "Procesador 8 núcleos con 3D V-Cache para gaming",
                "1500.00", "1850.00", 10, 2, "https://images-na.ssl-images-amazon.com/images/I/61vkFq7H9tL.jpg", catComponentes, marcaAMD, provTechDist);
        findOrCreateProducto("RAM-COR-32GB", "Corsair Vengeance 32GB DDR5 5600MHz", "Kit 2x16GB DDR5 para gaming y productividad",
                "350.00", "450.00", 25, 5, "https://pcya.pe/wp-content/uploads/2025/12/CMK32GX5M2F6000Z36-3.png", catComponentes, marcaCorsair, provCompuPeru);
        findOrCreateProducto("RAM-KIN-16GB", "Kingston FURY Beast 16GB DDR4 3200MHz", "Módulo 16GB DDR4 para upgrades de PC",
                "120.00", "170.00", 40, 10, "https://thumb.pccomponentes.com/w-530-530/articles/1065/10657909/3407-team-group-t-force-delta-rgb-ddr5-6000mhz-32gb-2x16gb-cl30-735bbe74-7dd0-4a77-b0b4-72c3a2f100f9.jpg", catComponentes, marcaKingston, provCompuPeru);
        findOrCreateProducto("MB-AS-ROG", "ASUS ROG Strix Z790-E Gaming WiFi", "Placa base ATX, LGA 1700, DDR5, WiFi 6E",
                "1400.00", "1750.00", 5, 2, "https://cdn.pixabay.com/photo/2015/03/21/06/28/motherboard-683247_1280.png", catComponentes, marcaAsus, provGamerStore);

        // Periféricos
        findOrCreateProducto("PER-LOG-G502", "Mouse Logitech G502 Hero", "Mouse gamer con sensor Hero 25K, 25600 DPI",
                "150.00", "220.00", 30, 5, "https://i.pinimg.com/736x/b1/ad/e7/b1ade7d0e476181b8c08d164522d7ac8.jpg", catPerifericos, marcaLogitech, provTechDist);
        findOrCreateProducto("PER-LOG-K520", "Teclado Logitech K520 Inalámbrico", "Teclado inalámbrico multimedia para oficina",
                "80.00", "120.00", 35, 8, "https://www.shutterstock.com/image-photo/gaming-keyboard-rgb-light-isolated-260nw-2172968125.jpg", catPerifericos, marcaLogitech, provTechDist);
        findOrCreateProducto("PER-HX-Cloud", "HyperX Cloud II 7.1", "Audífonos gamer con sonido envolvente 7.1, micrófono desmontable",
                "200.00", "290.00", 18, 4, "https://coolboxpe.vteximg.com.br/arquivos/ids/456381/Redragon-Cybill-H312_1.jpg?v=638840679255230000", catPerifericos, marcaHyperX, provGamerStore);
        findOrCreateProducto("PER-COR-K100", "Corsair K100 RGB", "Teclado mecánico gaming con switches OPX, RGB",
                "350.00", "480.00", 10, 2, "https://www.pngarts.com/files/4/Keyboard-PNG-Image.png", catPerifericos, marcaCorsair, provGamerStore);
        findOrCreateProducto("PER-LOG-PRO", "Logitech MX Master 3S", "Mouse inalámbrico productividad, MagSpeed, USB-C",
                "280.00", "380.00", 14, 3, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSp2T3m5vGuuPPZpEE6Z5jke4IhUuu1UdUKYrr4DdQsdfUNwATE2B9gDlw&s=10", catPerifericos, marcaLogitech, provTechDist);

        // Monitores
        findOrCreateProducto("MON-SAM-27", "Samsung Odyssey G5 27\" 165Hz", "Monitor gaming 27\" QHD 165Hz, 1ms, curvo 1000R",
                "850.00", "1050.00", 12, 3, "https://w7.pngwing.com/pngs/404/255/png-transparent-computer-monitors-4k-resolution-display-resolution-1080p-monitors-gadget-electronics-computer.png", catMonitores, marcaSamsung, provCompuPeru);
        findOrCreateProducto("MON-AS-27", "ASUS ROG Swift PG279QM 27\"", "Monitor gaming 27\" QHD 240Hz, IPS, G-Sync",
                "2200.00", "2700.00", 4, 1, "https://dlcdnwebimgs.asus.com/gain/72C16A36-4EE3-4AC4-A58A-35F6B8A2FB6F", catMonitores, marcaAsus, provGamerStore);
        findOrCreateProducto("MON-SAM-32", "Samsung ViewFinity S8 32\" 4K", "Monitor 32\" 4K UHD, USB-C, 99% sRGB para diseño",
                "1500.00", "1850.00", 7, 2, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQitgQfmWUlPD1xB9X4PYEYarezg67yKm2-XW22TAngFg&s=10", catMonitores, marcaSamsung, provCompuPeru);
        findOrCreateProducto("MON-LG-24", "LG 24MP400 24\" FHD", "Monitor 24\" Full HD, IPS, 75Hz, FreeSync",
                "450.00", "580.00", 20, 5, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSTOsADp5e1i-oCswKoidYSe0MMv-Ev8qvBztG7EMrQHpWCZQIkE6biNg&s=10", catMonitores, marcaSamsung, provCompuPeru);

        // Laptops
        findOrCreateProducto("LAP-AS-TUF", "ASUS TUF Gaming A15 FA507NV", "Laptop 15.6\" FHD 144Hz, Ryzen 7 7735HS, RTX 4060, 16GB, 512GB SSD",
                "3800.00", "4500.00", 6, 2, null, catLaptops, marcaAsus, provGamerStore);
        findOrCreateProducto("LAP-AS-VIVO", "ASUS VivoBook 15 X1504ZA", "Laptop 15.6\" FHD, Core i5-1235U, 8GB, 512GB SSD",
                "1600.00", "1950.00", 10, 3, null, catLaptops, marcaAsus, provTechDist);
        findOrCreateProducto("LAP-HP-15", "HP Laptop 15-fd0000la", "Laptop 15.6\" FHD, Core i5-1335U, 8GB, 256GB SSD",
                "1400.00", "1700.00", 8, 2, "https://png.pngtree.com/png-clipart/20240325/original/pngtree-hp-laptop-white-background-png-image_14681338.png", catLaptops, marcaIntel, provCompuPeru);

        // Redes
        findOrCreateProducto("RED-TP-AX", "TP-Link Archer AX55", "Router WiFi 6 dual band, 2402 Mbps, Gigabit",
                "180.00", "260.00", 22, 5, null, catRedes, marcaTP, provRedesTotal);
        findOrCreateProducto("RED-TP-SW", "TP-Link TL-SG105 Switch 5 Puertos", "Switch Gigabit 5 puertos, plug & play",
                "50.00", "80.00", 40, 10, null, catRedes, marcaTP, provRedesTotal);
        findOrCreateProducto("RED-TP-USB", "TP-Link Archer T3U Plus Adaptador WiFi USB", "Adaptador WiFi USB AC1300, doble banda, antena externa",
                "60.00", "95.00", 30, 8, null, catRedes, marcaTP, provRedesTotal);

        // Almacenamiento
        findOrCreateProducto("SSD-SAM-1TB", "Samsung 870 EVO 1TB SATA", "SSD SATA 2.5\", 560/530 MB/s, V-NAND",
                "280.00", "370.00", 20, 5, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR04xlno38qFw12f_lcAkJoe-X2Xpckoi9afycEr1Ki_A&s=10", catAlmacenamiento, marcaSamsung, provCompuPeru);
        findOrCreateProducto("SSD-KIN-500GB", "Kingston NV2 500GB NVMe", "SSD NVMe PCIe Gen4, 3500/2100 MB/s",
                "120.00", "175.00", 35, 8, "https://api.primusgaming.com/images/primus_arcus300s_360_PWH-300_1.png", catAlmacenamiento, marcaKingston, provCompuPeru);
        findOrCreateProducto("HDD-WD-2TB", "WD Blue 2TB 3.5\"", "Disco duro interno 2TB, 7200 RPM, SATA III",
                "160.00", "220.00", 15, 4, "https://discosduros.com.co/wp-content/uploads/2023/08/Seagate-BarraCuda-%E2%80%93-Disco-duro-Interno-de-2TB-SATA-%E2%80%93-3.5-discoduros.com_.co-1.png", catAlmacenamiento, marcaKingston, provTechDist);

        // Audio y Video
        findOrCreateProducto("AUD-HX-Alpha", "HyperX Alpha Wireless", "Audífonos gamer inalámbricos, 300h batería, DTS",
                "380.00", "500.00", 10, 2, "https://api.primusgaming.com/images/primus_arcus300s_360_PWH-300_1.png", catAudio, marcaHyperX, provGamerStore);
        findOrCreateProducto("SPK-LOG-Z506", "Logitech Z506 5.1 Surround", "Sistema de parlantes 5.1, 75W RMS, graves potentes",
                "200.00", "290.00", 12, 3, "https://http2.mlstatic.com/D_NQ_NP_744573-MPE40175138228_122019-O.webp", catAudio, marcaLogitech, provTechDist);

        // Accesorios
        findOrCreateProducto("ACC-COR-CX750", "Corsair CX750M 80+ Bronze", "Fuente de poder 750W modular, certificación 80+ Bronze",
                "250.00", "340.00", 18, 4, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQeeaZAA7jp_lNH-ZH252PXxxiDyo_lwZYtsU4sPqJghYDrdyPdbNe-J8I&s=10", catAccesorios, marcaCorsair, provCompuPeru);
        findOrCreateProducto("ACC-COR-4000D", "Corsair 4000D Airflow", "Gabinete ATX medio torre, excelente flujo de aire, templado lateral",
                "280.00", "370.00", 14, 3, "https://image.made-in-china.com/202f0j00wRpfLuhKJgze/OEM-Eatx-Full-Tower-Tempered-Glass-Side-Window-Panel-Computer-Gaming-Chassis-ATX-Case.webp", catAccesorios, marcaCorsair, provCompuPeru);

        // ===== Usuarios =====
        String password = passwordEncoder.encode("123456");

        Usuario usuarioAdmin = usuarioRepository.findByDni("70000001").orElseGet(() ->
                usuarioRepository.save(Usuario.builder()
                        .nombres("Johan").apellidos("Admin").dni("70000001")
                        .telefono("999888777").direccion("Av. Desarrollo 123").activo(true).build()));
        if (credencialRepository.findByCorreo("admin@sistema.edu.pe").isEmpty()) {
            credencialRepository.save(Credencial.builder()
                    .usuario(usuarioAdmin).correo("admin@sistema.edu.pe").password(password).activo(true).build());
            usuarioRolRepository.save(UsuarioRol.builder().usuario(usuarioAdmin).rol(admin).build());
        }

        Usuario usuarioCliente = usuarioRepository.findByDni("70000002").orElseGet(() ->
                usuarioRepository.save(Usuario.builder()
                        .nombres("Johan").apellidos("Joseph").dni("70000002")
                        .telefono("988777666").direccion("Calle Las Pruebas 404, SJL").activo(true).build()));
        if (credencialRepository.findByCorreo("johan@cliente.pe").isEmpty()) {
            credencialRepository.save(Credencial.builder()
                    .usuario(usuarioCliente).correo("johan@cliente.pe").password(password).activo(true).build());
            usuarioRolRepository.save(UsuarioRol.builder().usuario(usuarioCliente).rol(cliente).build());
        }

        Usuario usuarioRepartidor = usuarioRepository.findByDni("70000003").orElseGet(() ->
                usuarioRepository.save(Usuario.builder()
                        .nombres("Carlos").apellidos("Repartidor").dni("70000003")
                        .telefono("977666555").direccion("Av. Reparto 456, Comas").activo(true).build()));
        if (credencialRepository.findByCorreo("repartidor@sistema.edu.pe").isEmpty()) {
            credencialRepository.save(Credencial.builder()
                    .usuario(usuarioRepartidor).correo("repartidor@sistema.edu.pe").password(password).activo(true).build());
            usuarioRolRepository.save(UsuarioRol.builder().usuario(usuarioRepartidor).rol(repartidor).build());
        }

        log.info("=== Datos semilla inicializados correctamente ===");
        log.info("  Usuarios: admin@sistema.edu.pe / johan@cliente.pe / repartidor@sistema.edu.pe  (password: 123456)");
        log.info("  Categorías: {} | Marcas: {} | Proveedores: {} | Productos: {}",
                categoriaRepository.count(), marcaRepository.count(), proveedorRepository.count(), productoRepository.count());
        log.info("================================================");
    }

    private Rol findOrCreateRol(String nombre, String descripcion) {
        return rolRepository.findByNombre(nombre).orElseGet(() ->
                rolRepository.save(Rol.builder().nombre(nombre).descripcion(descripcion).activo(true).build()));
    }

    private MetodoPago findOrCreateMetodoPago(String nombre, String descripcion) {
        return metodoPagoRepository.findByNombre(nombre).orElseGet(() ->
                metodoPagoRepository.save(MetodoPago.builder().nombre(nombre).descripcion(descripcion).activo(true).build()));
    }

    private EstadoVenta findOrCreateEstadoVenta(String nombre, String descripcion) {
        return estadoVentaRepository.findByNombre(nombre).orElseGet(() ->
                estadoVentaRepository.save(EstadoVenta.builder().nombre(nombre).descripcion(descripcion).build()));
    }

    private EstadoPedido findOrCreateEstadoPedido(String nombre, String descripcion) {
        return estadoPedidoRepository.findByNombre(nombre).orElseGet(() ->
                estadoPedidoRepository.save(EstadoPedido.builder().nombre(nombre).descripcion(descripcion).build()));
    }

    private EstadoEnvio findOrCreateEstadoEnvio(String nombre, String descripcion) {
        return estadoEnvioRepository.findByNombre(nombre).orElseGet(() ->
                estadoEnvioRepository.save(EstadoEnvio.builder().nombre(nombre).descripcion(descripcion).build()));
    }

    private Categoria findOrCreateCategoria(String nombre, String descripcion) {
        return categoriaRepository.findByNombre(nombre).orElseGet(() ->
                categoriaRepository.save(Categoria.builder().nombre(nombre).descripcion(descripcion).activo(true).build()));
    }

    private Marca findOrCreateMarca(String nombre, String descripcion) {
        return marcaRepository.findByNombre(nombre).orElseGet(() ->
                marcaRepository.save(Marca.builder().nombre(nombre).descripcion(descripcion).activo(true).build()));
    }

    private Proveedor findOrCreateProveedor(String razonSocial, String ruc, String telefono, String correo, String direccion) {
        return proveedorRepository.findByRuc(ruc).orElseGet(() ->
                proveedorRepository.save(Proveedor.builder()
                        .razonSocial(razonSocial).ruc(ruc).telefono(telefono)
                        .correo(correo).direccion(direccion).activo(true).build()));
    }

    private Producto findOrCreateProducto(String codigo, String nombre, String descripcion,
                                         String precioCompra, String precioVenta,
                                         int stock, int stockMinimo, String imagen,
                                         Categoria categoria, Marca marca, Proveedor proveedor) {
        return productoRepository.findByCodigo(codigo).orElseGet(() ->
                productoRepository.save(Producto.builder()
                        .codigo(codigo).nombre(nombre).descripcion(descripcion)
                        .precioCompra(new BigDecimal(precioCompra)).precioVenta(new BigDecimal(precioVenta))
                        .stock(stock).stockMinimo(stockMinimo).imagen(imagen)
                        .categoria(categoria).marca(marca).proveedor(proveedor).activo(true).build()));
    }
}
