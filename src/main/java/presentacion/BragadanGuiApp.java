package presentacion;

import comun.MonedaUtils;
import dominio.builder.ComboBuilderImpl;
import dominio.builder.ComboDirector;
import dominio.excepcion.BragadanException;
import dominio.factory.ProductoFactoryRegistry;
import dominio.modelo.Combo;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Pedido;
import dominio.modelo.Producto;
import dominio.observer.*;
import dto.ProductoDTO;
import dto.ResumenPedidoDTO;
import infraestructura.config.MenuConfigLoader;
import infraestructura.repositorio.MemoriaMenuRepositorio;
import infraestructura.repositorio.MemoriaPedidoRepositorio;
import infraestructura.repositorio.MenuRepositorio;
import infraestructura.repositorio.PedidoRepositorio;
import servicio.MenuServicio;
import servicio.PedidoServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Aplicación de Escritorio Gráfica (GUI) para la cafetería Bragadan.
 * Permite interactuar visualmente con el menú, personalizar con el patrón Decorator,
 * armar combos con el patrón Builder/Director, y observar en tiempo real las notificaciones
 * de Cocina, Caja y Mesero con el patrón Observer.
 */
public class BragadanGuiApp extends JFrame {

    private final MenuServicio menuServicio;
    private final PedidoServicio pedidoServicio;
    private final ComboDirector comboDirector;

    // Estado actual de la UI
    private Long pedidoActualId;
    private Cocina cocinaObserver;
    private Caja cajaObserver;
    private Mesero meseroObserver;

    // Componentes gráficos
    private JTable tablaMenu;
    private DefaultTableModel modeloMenu;
    private JTable tablaPedido;
    private DefaultTableModel modeloPedido;
    private JLabel lblTotal;
    private JLabel lblEstado;
    private JLabel lblIdPedido;

    // Checkboxes de decoradores
    private JCheckBox chkShotExtra;
    private JCheckBox chkLecheAlmendra;
    private JCheckBox chkQuesoExtra;
    private JCheckBox chkCremaBatida;

    // Áreas de notificación para observadores
    private JTextArea txtCocina;
    private JTextArea txtCaja;
    private JTextArea txtMesero;

    // Botones de transición de estado
    private JButton btnPreparar;
    private JButton btnListo;
    private JButton btnEntregar;
    private JButton btnCancelar;

    public BragadanGuiApp() {
        super("☕ Cafetería Bragadan - Sistema de Gestión de Pedidos");

        // Inicialización de lógica de negocio y repositorios
        MenuRepositorio menuRepo = new MemoriaMenuRepositorio();
        MenuConfigLoader.cargarMenuInicial(menuRepo);
        ProductoFactoryRegistry registry = ProductoFactoryRegistry.conFabricasPorDefecto();
        this.menuServicio = new MenuServicio(menuRepo, registry);

        PedidoRepositorio pedidoRepo = new MemoriaPedidoRepositorio();
        this.pedidoServicio = new PedidoServicio(pedidoRepo, menuServicio);
        this.comboDirector = new ComboDirector(new ComboBuilderImpl());

        configurarVentana();
        inicializarComponentes();
        crearNuevoPedido();
        cargarMenuEnTabla();
    }

    private void configurarVentana() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // 1. ENCABEZADO SUPERIOR
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(44, 24, 16)); // Café oscuro elegante
        panelHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitulo = new JLabel("☕ CAFETERÍA GOURMET BRAGADAN");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(245, 230, 211));

        JPanel panelInfoPedido = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelInfoPedido.setOpaque(false);

        lblIdPedido = new JLabel("Pedido #0");
        lblIdPedido.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblIdPedido.setForeground(Color.WHITE);

        lblEstado = new JLabel("ESTADO: RECIBIDO");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstado.setForeground(new Color(255, 215, 0)); // Dorado

        JButton btnNuevoPedido = new JButton("➕ Nuevo Pedido");
        btnNuevoPedido.setFocusPainted(false);
        btnNuevoPedido.setBackground(new Color(139, 69, 19));
        btnNuevoPedido.setForeground(Color.WHITE);
        btnNuevoPedido.addActionListener(e -> crearNuevoPedido());

        panelInfoPedido.add(lblIdPedido);
        panelInfoPedido.add(lblEstado);
        panelInfoPedido.add(btnNuevoPedido);

        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(panelInfoPedido, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // 2. CUERPO CENTRAL (3 COLUMNAS: MENÚ | PERSONALIZACIÓN Y COMBOS | PEDIDO ACTUAL)
        JPanel panelCuerpo = new JPanel(new GridLayout(1, 3, 10, 10));
        panelCuerpo.setBorder(new EmptyBorder(0, 10, 0, 10));

        panelCuerpo.add(crearPanelMenu());
        panelCuerpo.add(crearPanelPersonalizacionYCombos());
        panelCuerpo.add(crearPanelPedidoActual());

        add(panelCuerpo, BorderLayout.CENTER);

        // 3. PANEL INFERIOR: OBSERVADORES EN TIEMPO REAL (Cocina, Caja, Mesero)
        add(crearPanelObservadores(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "1. Menú y Catálogo Bragadan",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(44, 24, 16)));

        modeloMenu = new DefaultTableModel(new Object[]{"Cód.", "Producto", "Precio", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMenu = new JTable(modeloMenu);
        tablaMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMenu.setRowHeight(24);
        tablaMenu.getColumnModel().getColumn(0).setPreferredWidth(55);
        tablaMenu.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaMenu.getColumnModel().getColumn(2).setPreferredWidth(70);
        tablaMenu.getColumnModel().getColumn(3).setPreferredWidth(60);

        JScrollPane scroll = new JScrollPane(tablaMenu);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnAgregarSimple = new JButton("➕ Agregar Producto al Pedido");
        btnAgregarSimple.setBackground(new Color(46, 125, 50));
        btnAgregarSimple.setForeground(Color.WHITE);
        btnAgregarSimple.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAgregarSimple.setFocusPainted(false);
        btnAgregarSimple.addActionListener(e -> agregarProductoSeleccionadoSimple());

        panel.add(btnAgregarSimple, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPersonalizacionYCombos() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "2. Personalización (Decorator) & Combos (Builder)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(44, 24, 16)));

        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Subsección Decorator
        JLabel lblDecoradores = new JLabel("✨ Adiciones (Patrón Decorator):");
        lblDecoradores.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDecoradores.setAlignmentX(Component.LEFT_ALIGNMENT);

        chkShotExtra = new JCheckBox("Shot Extra (+$1.500)");
        chkLecheAlmendra = new JCheckBox("Leche de Almendra (+$2.000)");
        chkQuesoExtra = new JCheckBox("Queso Extra (+$1.800)");
        chkCremaBatida = new JCheckBox("Crema Batida (+$1.200)");

        JButton btnAgregarConAdiciones = new JButton("⭐ Agregar Ítem con Adiciones");
        btnAgregarConAdiciones.setBackground(new Color(142, 68, 173));
        btnAgregarConAdiciones.setForeground(Color.WHITE);
        btnAgregarConAdiciones.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAgregarConAdiciones.setFocusPainted(false);
        btnAgregarConAdiciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAgregarConAdiciones.addActionListener(e -> agregarProductoConAdiciones());

        contenedor.add(lblDecoradores);
        contenedor.add(Box.createVerticalStrut(5));
        contenedor.add(chkShotExtra);
        contenedor.add(chkLecheAlmendra);
        contenedor.add(chkQuesoExtra);
        contenedor.add(chkCremaBatida);
        contenedor.add(Box.createVerticalStrut(8));
        contenedor.add(btnAgregarConAdiciones);

        contenedor.add(Box.createVerticalStrut(18));
        contenedor.add(new JSeparator(SwingConstants.HORIZONTAL));
        contenedor.add(Box.createVerticalStrut(10));

        // Subsección Builder / Director
        JLabel lblCombos = new JLabel("🍔 Combos Predefinidos (Builder + Director):");
        lblCombos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCombos.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenedor.add(lblCombos);
        contenedor.add(Box.createVerticalStrut(8));

        JButton btnComboDesayuno = new JButton("🥐 Combo Desayuno Bragadan (10% Dcto)");
        btnComboDesayuno.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnComboDesayuno.addActionListener(e -> agregarComboDirector("DESAYUNO"));

        JButton btnComboAlmuerzo = new JButton("🥪 Combo Almuerzo Gourmet (15% Dcto)");
        btnComboAlmuerzo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnComboAlmuerzo.addActionListener(e -> agregarComboDirector("ALMUERZO"));

        JButton btnComboMerienda = new JButton("🍰 Combo Merienda Bragadan (10% Dcto)");
        btnComboMerienda.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnComboMerienda.addActionListener(e -> agregarComboDirector("MERIENDA"));

        contenedor.add(btnComboDesayuno);
        contenedor.add(Box.createVerticalStrut(6));
        contenedor.add(btnComboAlmuerzo);
        contenedor.add(Box.createVerticalStrut(6));
        contenedor.add(btnComboMerienda);

        panel.add(new JScrollPane(contenedor), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPedidoActual() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "3. Pedido Actual & Facturación",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(44, 24, 16)));

        modeloPedido = new DefaultTableModel(new Object[]{"#", "Ítem / Descripción", "Precio"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPedido = new JTable(modeloPedido);
        tablaPedido.setRowHeight(26);
        tablaPedido.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaPedido.getColumnModel().getColumn(1).setPreferredWidth(190);
        tablaPedido.getColumnModel().getColumn(2).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(tablaPedido);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel panelInferiorPedido = new JPanel(new BorderLayout(5, 5));
        panelInferiorPedido.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblTotalTexto = new JLabel("TOTAL: ");
        lblTotalTexto.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(198, 40, 40));

        panelTotal.add(lblTotalTexto);
        panelTotal.add(lblTotal);
        panelInferiorPedido.add(panelTotal, BorderLayout.NORTH);

        JButton btnVerFactura = new JButton("🧾 Ver Factura Detallada");
        btnVerFactura.setBackground(new Color(33, 150, 243));
        btnVerFactura.setForeground(Color.WHITE);
        btnVerFactura.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerFactura.setFocusPainted(false);
        btnVerFactura.addActionListener(e -> mostrarFacturaModal());

        panelInferiorPedido.add(btnVerFactura, BorderLayout.SOUTH);
        panel.add(panelInferiorPedido, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelObservadores() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "4. Ciclo de Vida y Notificaciones en Tiempo Real (Patrón Observer: Cocina, Caja, Mesero)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(44, 24, 16)));
        panel.setPreferredSize(new Dimension(1160, 250));

        // Botones de acción del ciclo de vida
        JPanel panelBotonesEstado = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
        panelBotonesEstado.setBackground(new Color(245, 245, 245));

        btnPreparar = new JButton("👨‍🍳 1. Pasar a EN PREPARACIÓN");
        btnPreparar.setBackground(new Color(255, 152, 0));
        btnPreparar.setForeground(Color.WHITE);
        btnPreparar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPreparar.addActionListener(e -> transicionarEstado(EstadoPedido.EN_PREPARACION));

        btnListo = new JButton("🔔 2. Marcar como LISTO");
        btnListo.setBackground(new Color(0, 150, 136));
        btnListo.setForeground(Color.WHITE);
        btnListo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnListo.addActionListener(e -> transicionarEstado(EstadoPedido.LISTO));

        btnEntregar = new JButton("✅ 3. Marcar como ENTREGADO");
        btnEntregar.setBackground(new Color(76, 175, 80));
        btnEntregar.setForeground(Color.WHITE);
        btnEntregar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEntregar.addActionListener(e -> transicionarEstado(EstadoPedido.ENTREGADO));

        btnCancelar = new JButton("❌ Cancelar Pedido");
        btnCancelar.setBackground(new Color(244, 67, 54));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelar.addActionListener(e -> transicionarEstado(EstadoPedido.CANCELADO));

        panelBotonesEstado.add(btnPreparar);
        panelBotonesEstado.add(btnListo);
        panelBotonesEstado.add(btnEntregar);
        panelBotonesEstado.add(btnCancelar);

        panel.add(panelBotonesEstado, BorderLayout.NORTH);

        // 3 Áreas de texto para observar la actividad de Cocina, Caja y Mesero
        JPanel panelLogs = new JPanel(new GridLayout(1, 3, 8, 0));
        panelLogs.setBorder(new EmptyBorder(5, 5, 5, 5));

        txtCocina = crearAreaTextoLog("👨‍🍳 Notificaciones de Cocina:");
        txtCaja = crearAreaTextoLog("💳 Notificaciones de Caja:");
        txtMesero = crearAreaTextoLog("🤵 Notificaciones de Mesero:");

        panelLogs.add(crearScrollConTitulo(txtCocina, "👨‍🍳 COCINA"));
        panelLogs.add(crearScrollConTitulo(txtCaja, "💳 CAJA"));
        panelLogs.add(crearScrollConTitulo(txtMesero, "🤵 MESERO"));

        panel.add(panelLogs, BorderLayout.CENTER);
        return panel;
    }

    private JTextArea crearAreaTextoLog(String titulo) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(250, 250, 250));
        return area;
    }

    private JPanel crearScrollConTitulo(JTextArea area, String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), titulo));
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // --- ACCIONES Y LÓGICA DE NEGOCIO ---

    private void crearNuevoPedido() {
        this.cocinaObserver = new Cocina();
        this.cajaObserver = new Caja();
        this.meseroObserver = new Mesero();

        List<PedidoObserver> obs = List.of(cocinaObserver, cajaObserver, meseroObserver);
        var pedidoDTO = pedidoServicio.crearPedidoConObservadores(obs);
        this.pedidoActualId = pedidoDTO.id();

        lblIdPedido.setText("Pedido #" + pedidoActualId);
        actualizarEstadoUI(EstadoPedido.RECIBIDO);
        refrescarTablaPedido();

        txtCocina.setText("• Observador de Cocina conectado y listo.\n");
        txtCaja.setText("• Observador de Caja conectado y listo.\n");
        txtMesero.setText("• Observador de Mesero conectado y listo.\n");
    }

    private void cargarMenuEnTabla() {
        modeloMenu.setRowCount(0);
        List<ProductoDTO> items = menuServicio.consultarMenu();
        for (ProductoDTO p : items) {
            modeloMenu.addRow(new Object[]{p.codigo(), p.nombre(), p.precioFormateado(), p.tipo()});
        }
    }

    private void agregarProductoSeleccionadoSimple() {
        int fila = tablaMenu.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del menú primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modeloMenu.getValueAt(fila, 0);
        try {
            pedidoServicio.agregarItemPorCodigo(pedidoActualId, codigo);
            refrescarTablaPedido();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al agregar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProductoConAdiciones() {
        int fila = tablaMenu.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del menú base.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = (String) modeloMenu.getValueAt(fila, 0);
        Producto producto = menuServicio.crearProducto(codigo);

        List<String> adiciones = new ArrayList<>();
        if (chkShotExtra.isSelected()) adiciones.add("SHOT_EXTRA");
        if (chkLecheAlmendra.isSelected()) adiciones.add("LECHE_ALMENDRA");
        if (chkQuesoExtra.isSelected()) adiciones.add("QUESO");
        if (chkCremaBatida.isSelected()) adiciones.add("CREMA");

        if (adiciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos una adición de los checkboxes.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Producto decorado = pedidoServicio.personalizar(producto, adiciones);
        try {
            pedidoServicio.agregarItem(pedidoActualId, decorado);
            refrescarTablaPedido();
            limpiarCheckboxes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al agregar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarComboDirector(String tipoCombo) {
        try {
            Combo combo = switch (tipoCombo) {
                case "DESAYUNO" -> comboDirector.armarDesayuno(
                        menuServicio.crearProducto("PLA-01"), // Croissant
                        menuServicio.crearProducto("BEB-03"), // Cappuccino
                        menuServicio.crearProducto("PLA-04")  // Quiche
                );
                case "ALMUERZO" -> comboDirector.armarAlmuerzo(
                        menuServicio.crearProducto("PLA-02"), // Sándwich Gourmet
                        menuServicio.crearProducto("BEB-04"), // Cold Brew
                        menuServicio.crearProducto("PLA-04"), // Quiche
                        menuServicio.crearProducto("POS-01")  // Torta Zanahoria
                );
                case "MERIENDA" -> comboDirector.armarMerienda(
                        menuServicio.crearProducto("PLA-01"), // Croissant
                        menuServicio.crearProducto("BEB-01"), // Espresso
                        menuServicio.crearProducto("POS-02")  // Brownie
                );
                default -> throw new IllegalArgumentException("Combo desconocido");
            };

            pedidoServicio.agregarItem(pedidoActualId, combo);
            refrescarTablaPedido();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al armar combo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void transicionarEstado(EstadoPedido nuevoEstado) {
        try {
            pedidoServicio.cambiarEstado(pedidoActualId, nuevoEstado);
            actualizarEstadoUI(nuevoEstado);
            actualizarLogsObservadores();
        } catch (BragadanException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transición Inválida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstadoUI(EstadoPedido estado) {
        lblEstado.setText("ESTADO: " + estado.name());

        Color colorEstado = switch (estado) {
            case RECIBIDO -> new Color(255, 215, 0);
            case EN_PREPARACION -> new Color(255, 152, 0);
            case LISTO -> new Color(0, 188, 212);
            case ENTREGADO -> new Color(76, 175, 80);
            case CANCELADO -> new Color(244, 67, 54);
        };
        lblEstado.setForeground(colorEstado);

        // Habilitar / deshabilitar botones según las transiciones válidas
        btnPreparar.setEnabled(estado.puedePasarA(EstadoPedido.EN_PREPARACION));
        btnListo.setEnabled(estado.puedePasarA(EstadoPedido.LISTO));
        btnEntregar.setEnabled(estado.puedePasarA(EstadoPedido.ENTREGADO));
        btnCancelar.setEnabled(estado.puedePasarA(EstadoPedido.CANCELADO));
    }

    private void actualizarLogsObservadores() {
        if (!cocinaObserver.getNotificaciones().isEmpty()) {
            NotificacionEvento ev = cocinaObserver.getNotificaciones().getLast();
            txtCocina.append("• " + ev.mensaje() + "\n");
        }
        if (!cajaObserver.getNotificaciones().isEmpty()) {
            NotificacionEvento ev = cajaObserver.getNotificaciones().getLast();
            txtCaja.append("• " + ev.mensaje() + "\n");
        }
        if (!meseroObserver.getNotificaciones().isEmpty()) {
            NotificacionEvento ev = meseroObserver.getNotificaciones().getLast();
            txtMesero.append("• " + ev.mensaje() + "\n");
        }
    }

    private void refrescarTablaPedido() {
        modeloPedido.setRowCount(0);
        Pedido pedido = pedidoServicio.obtenerPedidoEntidad(pedidoActualId);
        List<Producto> items = pedido.getItems();

        for (int i = 0; i < items.size(); i++) {
            Producto item = items.get(i);
            modeloPedido.addRow(new Object[]{
                    (i + 1),
                    item.getNombre() + " (" + item.getDescripcion() + ")",
                    MonedaUtils.formatear(item.getPrecio())
            });
        }

        lblTotal.setText(MonedaUtils.formatear(pedido.calcularTotal()));
    }

    private void mostrarFacturaModal() {
        ResumenPedidoDTO resumen = pedidoServicio.obtenerResumen(pedidoActualId);
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("         FACTURA DE VENTA - CAFETERÍA BRAGADAN      \n");
        sb.append("====================================================\n");
        sb.append("Pedido #: ").append(resumen.pedidoId()).append("   Estado: ").append(resumen.estado()).append("\n");
        sb.append("Fecha: ").append(resumen.fechaHora().toLocalDate()).append("\n");
        sb.append("----------------------------------------------------\n");
        for (String linea : resumen.lineasDetalle()) {
            sb.append(linea).append("\n");
        }
        sb.append("----------------------------------------------------\n");
        sb.append("TOTAL A PAGAR: ").append(resumen.totalFormateado()).append("\n");
        sb.append("====================================================\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        area.setEditable(false);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 350));
        JOptionPane.showMessageDialog(this, scroll, "Factura Bragadan", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarCheckboxes() {
        chkShotExtra.setSelected(false);
        chkLecheAlmendra.setSelected(false);
        chkQuesoExtra.setSelected(false);
        chkCremaBatida.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BragadanGuiApp app = new BragadanGuiApp();
            app.setVisible(true);
        });
    }
}
