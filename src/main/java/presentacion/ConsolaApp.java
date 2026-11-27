package presentacion;

import com.bragadan.comun.MonedaUtils;
import com.bragadan.dominio.builder.ComboBuilder;
import com.bragadan.dominio.builder.ComboBuilderImpl;
import com.bragadan.dominio.builder.ComboDirector;
import com.bragadan.dominio.decorator.Crema;
import com.bragadan.dominio.decorator.LecheAlmendra;
import com.bragadan.dominio.decorator.ShotExtra;
import com.bragadan.dominio.excepcion.BragadanException;
import com.bragadan.dominio.factory.ConfigProducto;
import com.bragadan.dominio.factory.ProductoFactoryRegistry;
import com.bragadan.dominio.modelo.*;
import com.bragadan.dominio.observer.Caja;
import com.bragadan.dominio.observer.Cocina;
import com.bragadan.dominio.observer.Mesero;
import com.bragadan.dto.CambioEstadoRequestDTO;
import com.bragadan.dto.PedidoDTO;
import com.bragadan.dto.ProductoDTO;
import com.bragadan.dto.ResumenPedidoDTO;
import com.bragadan.infraestructura.config.MenuConfigLoader;
import com.bragadan.infraestructura.repositorio.MemoriaMenuRepositorio;
import com.bragadan.infraestructura.repositorio.MemoriaPedidoRepositorio;
import com.bragadan.infraestructura.repositorio.MenuRepositorio;
import com.bragadan.infraestructura.repositorio.PedidoRepositorio;
import com.bragadan.servicio.MenuServicio;
import com.bragadan.servicio.PedidoServicio;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Aplicación y punto de entrada para la cafetería Bragadan.
 * Soporta ejecución en tres modalidades:
 * 1. Aplicación de Escritorio con Ventana Gráfica (GUI) por defecto.
 * 2. Consola interactiva paso a paso con menú de usuario (--console / -c).
 * 3. Demostración automatizada completa (--demo).
 */
public class ConsolaApp {

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            String modo = args[0].trim().toLowerCase();
            switch (modo) {
                case "--demo" -> {
                    ejecutarDemostracion();
                    return;
                }
                case "--console", "--consola", "-c" -> {
                    ejecutarModoInteractivo();
                    return;
                }
                case "--gui", "-g" -> {
                    iniciarGui(args);
                    return;
                }
                default -> {
                    // Continuar al comportamiento por defecto
                }
            }
        }

        // Por defecto: abre la GUI de escritorio si no estamos en entorno headless
        if (!GraphicsEnvironment.isHeadless()) {
            iniciarGui(args);
        } else {
            ejecutarDemostracion();
        }
    }

    private static void iniciarGui(String[] args) {
        BragadanGuiApp.main(args);
    }

    /**
     * Modo interactivo para usar la cafetería Bragadan directamente desde la consola o terminal.
     */
    public static void ejecutarModoInteractivo() {
        imprimirEncabezado();
        MenuRepositorio menuRepo = new MemoriaMenuRepositorio();
        MenuConfigLoader.cargarMenuInicial(menuRepo);
        ProductoFactoryRegistry registry = ProductoFactoryRegistry.conFabricasPorDefecto();
        MenuServicio menuServicio = new MenuServicio(menuRepo, registry);

        PedidoRepositorio pedidoRepo = new MemoriaPedidoRepositorio();
        PedidoServicio pedidoServicio = new PedidoServicio(pedidoRepo, menuServicio);
        ComboDirector comboDirector = new ComboDirector(new ComboBuilderImpl());

        Cocina cocina = new Cocina();
        Caja caja = new Caja();
        Mesero mesero = new Mesero();
        PedidoDTO pedidoActual = pedidoServicio.crearPedidoConObservadores(List.of(cocina, caja, mesero));
        Long pedidoId = pedidoActual.id();

        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        System.out.printf("  ✓ Se ha inicializado un nuevo pedido activo: #%d%n", pedidoId);

        while (continuar) {
            System.out.println("\n------------------------------------------------------------");
            System.out.println("  ☕ MENÚ PRINCIPAL - CAFETERÍA BRAGADAN (Pedido #" + pedidoId + ")");
            System.out.println("------------------------------------------------------------");
            System.out.println("  1. Ver Catálogo del Menú");
            System.out.println("  2. Agregar Producto Simple al Pedido");
            System.out.println("  3. Agregar Producto con Personalizaciones (Decorator)");
            System.out.println("  4. Agregar Combo Especial (Builder + Director)");
            System.out.println("  5. Ver Resumen del Pedido y Factura");
            System.out.println("  6. Cambiar Estado del Pedido (Observer en acción)");
            System.out.println("  7. Ver Alertas de Observadores (Cocina, Caja, Mesero)");
            System.out.println("  8. Crear Nuevo Pedido");
            System.out.println("  9. Salir");
            System.out.print("  Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> {
                    System.out.println("\n--- CATÁLOGO DISPONIBLE EN BRAGADAN ---");
                    for (ProductoDTO p : menuServicio.consultarMenu()) {
                        System.out.printf("  • [%s] %-28s %s (%s)%n",
                                p.codigo(), p.nombre(), p.precioFormateado(), p.tipo());
                    }
                }
                case "2" -> {
                    System.out.print("  Ingrese el código del producto (ej: BEB-01, PLA-01, POS-01): ");
                    String cod = scanner.nextLine().trim().toUpperCase();
                    try {
                        pedidoServicio.agregarItemPorCodigo(pedidoId, cod);
                        PedidoDTO act = pedidoServicio.obtenerPedido(pedidoId);
                        System.out.println("  ✓ Producto agregado. Total actual: " + act.totalFormateado());
                    } catch (Exception e) {
                        System.out.println("  ❌ Error: " + e.getMessage());
                    }
                }
                case "3" -> {
                    System.out.print("  Ingrese código del producto base (ej: BEB-01): ");
                    String codBase = scanner.nextLine().trim().toUpperCase();
                    try {
                        Producto base = menuServicio.crearProducto(codBase);
                        List<String> adiciones = new ArrayList<>();
                        System.out.println("  ¿Desea Shot Extra? (s/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("s")) adiciones.add("SHOT_EXTRA");
                        System.out.println("  ¿Desea Leche de Almendras? (s/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("s")) adiciones.add("LECHE_ALMENDRA");
                        System.out.println("  ¿Desea Queso Extra? (s/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("s")) adiciones.add("QUESO");
                        System.out.println("  ¿Desea Crema Batida? (s/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("s")) adiciones.add("CREMA");

                        Producto decorado = pedidoServicio.personalizar(base, adiciones);
                        pedidoServicio.agregarItem(pedidoId, decorado);
                        System.out.println("  ✓ Ítem decorado agregado: " + decorado.getDescripcion());
                        System.out.println("    Precio: " + MonedaUtils.formatear(decorado.getPrecio()));
                    } catch (Exception e) {
                        System.out.println("  ❌ Error: " + e.getMessage());
                    }
                }
                case "4" -> {
                    System.out.println("  Seleccione el combo a preparar:");
                    System.out.println("  1. Combo Desayuno (Croissant + Cappuccino + Quiche) - 10% Dcto");
                    System.out.println("  2. Combo Almuerzo Gourmet (Sándwich + Cold Brew + Quiche + Torta) - 15% Dcto");
                    System.out.println("  3. Combo Merienda (Croissant + Espresso + Brownie) - 10% Dcto");
                    System.out.print("  Opción: ");
                    String opCombo = scanner.nextLine().trim();
                    try {
                        Combo combo = switch (opCombo) {
                            case "1" -> comboDirector.armarDesayuno(
                                    menuServicio.crearProducto("PLA-01"),
                                    menuServicio.crearProducto("BEB-03"),
                                    menuServicio.crearProducto("PLA-04"));
                            case "2" -> comboDirector.armarAlmuerzo(
                                    menuServicio.crearProducto("PLA-02"),
                                    menuServicio.crearProducto("BEB-04"),
                                    menuServicio.crearProducto("PLA-04"),
                                    menuServicio.crearProducto("POS-01"));
                            case "3" -> comboDirector.armarMerienda(
                                    menuServicio.crearProducto("PLA-01"),
                                    menuServicio.crearProducto("BEB-01"),
                                    menuServicio.crearProducto("POS-02"));
                            default -> throw new IllegalArgumentException("Opción inválida");
                        };
                        pedidoServicio.agregarItem(pedidoId, combo);
                        System.out.println("  ✓ " + combo.getNombre() + " agregado al pedido.");
                    } catch (Exception e) {
                        System.out.println("  ❌ Error: " + e.getMessage());
                    }
                }
                case "5" -> {
                    ResumenPedidoDTO resumen = pedidoServicio.obtenerResumen(pedidoId);
                    imprimirFactura(resumen);
                }
                case "6" -> {
                    System.out.println("  Estados disponibles: EN_PREPARACION, LISTO, ENTREGADO, CANCELADO");
                    System.out.print("  Ingrese nuevo estado: ");
                    String est = scanner.nextLine().trim().toUpperCase();
                    try {
                        pedidoServicio.cambiarEstado(pedidoId, new CambioEstadoRequestDTO(est));
                        System.out.println("  ✓ Estado actualizado a: " + est);
                        mostrarUltimasNotificaciones(cocina, caja, mesero);
                    } catch (Exception e) {
                        System.out.println("  ❌ Error: " + e.getMessage());
                    }
                }
                case "7" -> {
                    System.out.println("\n--- NOTIFICACIONES DE COCINA ---");
                    cocina.getNotificaciones().forEach(n -> System.out.println("  • " + n.mensaje()));
                    System.out.println("\n--- NOTIFICACIONES DE CAJA ---");
                    caja.getNotificaciones().forEach(n -> System.out.println("  • " + n.mensaje()));
                    System.out.println("\n--- NOTIFICACIONES DE MESERO ---");
                    mesero.getNotificaciones().forEach(n -> System.out.println("  • " + n.mensaje()));
                }
                case "8" -> {
                    cocina = new Cocina();
                    caja = new Caja();
                    mesero = new Mesero();
                    pedidoActual = pedidoServicio.crearPedidoConObservadores(List.of(cocina, caja, mesero));
                    pedidoId = pedidoActual.id();
                    System.out.println("  ✓ Nuevo pedido iniciado: #" + pedidoId);
                }
                case "9" -> {
                    continuar = false;
                    System.out.println("  ¡Gracias por usar el sistema de la Cafetería Bragadan!");
                }
                default -> System.out.println("  Opción no reconocida.");
            }
        }
    }

    /**
     * Demostración automatizada de todos los patrones y componentes de Bragadan.
     */
    public static void ejecutarDemostracion() {
        imprimirEncabezado();

        // 1. Inicialización de Infraestructura y Fábricas
        MenuRepositorio menuRepositorio = new MemoriaMenuRepositorio();
        MenuConfigLoader.cargarMenuInicial(menuRepositorio);
        ProductoFactoryRegistry factoryRegistry = ProductoFactoryRegistry.conFabricasPorDefecto();

        // 2. Inicialización de Servicios
        MenuServicio menuServicio = new MenuServicio(menuRepositorio, factoryRegistry);
        PedidoRepositorio pedidoRepositorio = new MemoriaPedidoRepositorio();
        PedidoServicio pedidoServicio = new PedidoServicio(pedidoRepositorio, menuServicio);

        // 3. Catálogo del Menú Bragadan
        imprimirSeccion("1. CATÁLOGO INICIAL DEL MENÚ BRAGADAN (DTOs)");
        List<ProductoDTO> menu = menuServicio.consultarMenu();
        menu.forEach(p -> System.out.printf("  • [%s] %-28s %-10s | %s%n",
                p.codigo(), p.nombre(), p.precioFormateado(), p.descripcion()));

        // 4. Factory Method + Registry: Creación y Modificación Dinámica
        imprimirSeccion("2. PATRÓN FACTORY METHOD + REGISTRY (Creación y Modificación)");
        ConfigProducto nuevoTe = new ConfigProducto(
                "BEB-06", "Té Chai Especiado",
                "Infusión aromática de té negro con cardamomo, canela y leche vaporizada",
                new BigDecimal("6800.00"), TipoProducto.BEBIDA,
                Map.of("temperatura", TemperaturaBebida.CALIENTE)
        );
        menuServicio.agregarProducto(nuevoTe);
        System.out.println("  ✓ Producto dinámico agregado al catálogo: " + nuevoTe.getNombre());

        // Modificación de características
        ConfigProducto teModificado = nuevoTe.clonar();
        teModificado.setNombre("Té Chai Especiado Imperial");
        teModificado.setPrecioBase(new BigDecimal("7200.00"));
        menuServicio.modificarProducto("BEB-06", teModificado);
        Producto teInstanciado = menuServicio.crearProducto("BEB-06");
        System.out.println("  ✓ Producto modificado e instanciado: " + teInstanciado);

        // 5. Patrón Decorator: Personalización de Productos
        imprimirSeccion("3. PATRÓN DECORATOR (Personalización Acumulativa)");
        Producto espresso = menuServicio.crearProducto("BEB-01");
        System.out.println("  Base:           " + espresso.getNombre() + " -> " + espresso.getPrecio());

        Producto conShot = new ShotExtra(espresso);
        System.out.println("  + Shot Extra:   " + conShot.getDescripcion() + " -> " + conShot.getPrecio());

        Producto conLeche = new LecheAlmendra(conShot);
        System.out.println("  + Almendras:    " + conLeche.getDescripcion() + " -> " + conLeche.getPrecio());

        Producto bebidaSuprema = new Crema(conLeche);
        System.out.println("  + Crema Batida: " + bebidaSuprema.getDescripcion() + " -> " + bebidaSuprema.getPrecio());

        // 6. Patrón Builder + Director: Armado de Combos
        imprimirSeccion("4. PATRÓN BUILDER + DIRECTOR (Combos Compuestos)");
        ComboBuilder comboBuilder = new ComboBuilderImpl();
        ComboDirector director = new ComboDirector(comboBuilder);

        Producto croissant = menuServicio.crearProducto("PLA-01");
        Producto capuccino = menuServicio.crearProducto("BEB-03");
        Producto quiche = menuServicio.crearProducto("PLA-04");
        Producto torta = menuServicio.crearProducto("POS-01");

        Combo comboDesayuno = director.armarDesayuno(croissant, capuccino, quiche);
        System.out.println("  ✓ Combo armado por Director: " + comboDesayuno.getNombre());
        System.out.println("    Descripción: " + comboDesayuno.getDescripcion());
        System.out.println("    Total con 10% dcto: $" + comboDesayuno.getPrecio());

        Combo comboAlmuerzo = director.armarAlmuerzo(
                menuServicio.crearProducto("PLA-02"),
                menuServicio.crearProducto("BEB-04"),
                quiche,
                torta
        );
        System.out.println("  ✓ Combo Almuerzo Gourmet armado: " + comboAlmuerzo.getNombre());
        System.out.println("    Total con 15% dcto: $" + comboAlmuerzo.getPrecio());

        // 7. Patrón Observer y Ciclo de Vida del Pedido
        imprimirSeccion("5. GESTIÓN DE PEDIDO Y NOTIFICACIÓN A OBSERVADORES (Cocina, Caja, Mesero)");
        Cocina cocina = new Cocina();
        Caja caja = new Caja();
        Mesero mesero = new Mesero();

        PedidoDTO nuevoPedidoDTO = pedidoServicio.crearPedidoConObservadores(List.of(cocina, caja, mesero));
        Long pedidoId = nuevoPedidoDTO.id();
        System.out.printf("  ✓ Pedido #%d creado en estado: %s%n", pedidoId, nuevoPedidoDTO.estado());

        // Agregamos ítems: simple, personalizado y combo
        pedidoServicio.agregarItem(pedidoId, bebidaSuprema);
        pedidoServicio.agregarItem(pedidoId, comboDesayuno);
        pedidoServicio.agregarItemPorCodigo(pedidoId, "POS-02"); // Brownie

        // Mostramos factura/resumen de Bragadan
        ResumenPedidoDTO resumen = pedidoServicio.obtenerResumen(pedidoId);
        imprimirFactura(resumen);

        // Transiciones de estado y disparo de observadores
        System.out.println("  --- Transición 1: RECIBIDO -> EN_PREPARACION ---");
        pedidoServicio.cambiarEstado(pedidoId, new CambioEstadoRequestDTO("EN_PREPARACION"));
        mostrarUltimasNotificaciones(cocina, caja, mesero);

        System.out.println("\n  --- Transición 2: EN_PREPARACION -> LISTO ---");
        pedidoServicio.cambiarEstado(pedidoId, new CambioEstadoRequestDTO("LISTO"));
        mostrarUltimasNotificaciones(cocina, caja, mesero);

        System.out.println("\n  --- Transición 3: LISTO -> ENTREGADO ---");
        pedidoServicio.cambiarEstado(pedidoId, new CambioEstadoRequestDTO("ENTREGADO"));
        mostrarUltimasNotificaciones(cocina, caja, mesero);

        // 8. Demostración de Robustez: Manejo de Excepciones del Dominio
        imprimirSeccion("6. CONTROL DE ROBUSTEZ Y EXCEPCIONES DE DOMINIO");
        try {
            System.out.println("  Intentando transición inválida: ENTREGADO -> EN_PREPARACION...");
            pedidoServicio.cambiarEstado(pedidoId, EstadoPedido.EN_PREPARACION);
        } catch (BragadanException e) {
            System.out.println("  ✓ Excepción capturada correctamente: " + e.getMessage());
        }

        try {
            System.out.println("  Intentando construir Combo sin bebida obligatoria...");
            comboBuilder.reiniciar().agregarPrincipal(croissant).build();
        } catch (BragadanException e) {
            System.out.println("  ✓ Excepción capturada correctamente: " + e.getMessage());
        }

        // 9. Estado Final
        imprimirSeccion("7. ESTADO FINAL DEL SISTEMA BRAGADAN");
        List<PedidoDTO> activos = pedidoServicio.pedidosActivos();
        System.out.println("  Pedidos activos en cocina/salón: " + activos.size());
        System.out.println("  Demostración de Cafetería Bragadan completada exitosamente.");
    }

    private static void imprimirEncabezado() {
        System.out.println("================================================================================");
        System.out.println("                 ☕  CAFETERÍA GOURMET BRAGADAN  ☕                         ");
        System.out.println("               Sistema Integral de Gestión de Pedidos                           ");
        System.out.println("================================================================================");
    }

    private static void imprimirSeccion(String titulo) {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("  " + titulo);
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void mostrarUltimasNotificaciones(Cocina cocina, Caja caja, Mesero mesero) {
        if (!cocina.getNotificaciones().isEmpty()) {
            System.out.println("    " + cocina.getNotificaciones().getLast().mensaje());
        }
        if (!caja.getNotificaciones().isEmpty()) {
            System.out.println("    " + caja.getNotificaciones().getLast().mensaje());
        }
        if (!mesero.getNotificaciones().isEmpty()) {
            System.out.println("    " + mesero.getNotificaciones().getLast().mensaje());
        }
    }

    private static void imprimirFactura(ResumenPedidoDTO resumen) {
        System.out.println("\n  +--------------------------------------------------------------------------+");
        System.out.println("  |                  FACTURA DE VENTA - CAFETERÍA BRAGADAN                   |");
        System.out.println("  | Pedido #: " + String.format("%-10d", resumen.pedidoId())
                + "  Estado: " + String.format("%-15s", resumen.estado())
                + "  Fecha: " + resumen.fechaHora().toLocalDate() + " |");
        System.out.println("  +--------------------------------------------------------------------------+");
        for (String linea : resumen.lineasDetalle()) {
            System.out.println("  |" + String.format(" %-72s", linea) + " |");
        }
        System.out.println("  +--------------------------------------------------------------------------+");
        System.out.println("  | TOTAL A PAGAR: " + String.format("%-56s", resumen.totalFormateado()) + " |");
        System.out.println("  +--------------------------------------------------------------------------+\n");
    }
}
