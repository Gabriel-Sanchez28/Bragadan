package dominio.modelo;

import com.bragadan.comun.MonedaUtils;
import com.bragadan.dominio.excepcion.TransicionEstadoInvalidaException;
import com.bragadan.dominio.observer.PedidoObserver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad principal que representa un pedido en la cafetería Bragadan.
 * Actúa como Sujeto (Subject) en el patrón Observer, gestionando suscriptores
 * y notificándoles ante transiciones válidas de su ciclo de vida.
 */
public class Pedido {

    private final Long id;
    private EstadoPedido estado;
    private final List<Producto> items;
    private final List<PedidoObserver> observadores;
    private final LocalDateTime fechaCreacion;

    public Pedido(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo");
        }
        this.id = id;
        this.estado = EstadoPedido.RECIBIDO;
        this.items = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Agrega un producto al pedido. Solo se permite mientras el pedido esté en estado RECIBIDO.
     */
    public void agregarItem(Producto producto) {
        Objects.requireNonNull(producto, "El producto a agregar no puede ser nulo");
        if (estado != EstadoPedido.RECIBIDO) {
            throw new IllegalStateException("No se pueden agregar productos a un pedido en estado: " + estado);
        }
        this.items.add(producto);
    }

    /**
     * Remueve un ítem del pedido por su índice. Solo permitido en estado RECIBIDO.
     */
    public void removerItem(int indice) {
        if (estado != EstadoPedido.RECIBIDO) {
            throw new IllegalStateException("No se pueden remover productos de un pedido en estado: " + estado);
        }
        if (indice < 0 || indice >= items.size()) {
            throw new IndexOutOfBoundsException("Índice de ítem fuera de rango: " + indice);
        }
        this.items.remove(indice);
    }

    /**
     * Retorna una vista inmutable de los productos del pedido.
     */
    public List<Producto> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Calcula el monto total del pedido sumando todos los ítems con escala y redondeo correctos.
     */
    public BigDecimal calcularTotal() {
        BigDecimal total = items.stream()
                .map(Producto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return MonedaUtils.redondear(total);
    }

    /**
     * Ejecuta una transición de estado si es válida según las reglas del negocio.
     * Al realizar una transición válida, notifica inmediatamente a los observadores registrados.
     */
    public void cambiarEstado(EstadoPedido nuevoEstado) {
        Objects.requireNonNull(nuevoEstado, "El nuevo estado no puede ser nulo");

        if (!estado.puedePasarA(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(this.estado, nuevoEstado);
        }

        EstadoPedido estadoAnterior = this.estado;
        this.estado = nuevoEstado;
        notificarObservadores(estadoAnterior);
    }

    /**
     * Registra un nuevo observador en el pedido.
     */
    public void agregarObservador(PedidoObserver observador) {
        Objects.requireNonNull(observador, "El observador no puede ser nulo");
        if (!observadores.contains(observador)) {
            this.observadores.add(observador);
        }
    }

    /**
     * Da de baja a un observador.
     */
    public void removerObservador(PedidoObserver observador) {
        this.observadores.remove(observador);
    }

    /**
     * Retorna una vista inmutable de los observadores registrados.
     */
    public List<PedidoObserver> getObservadores() {
        return Collections.unmodifiableList(observadores);
    }

    /**
     * Notifica a todos los observadores registrados el cambio de estado.
     */
    private void notificarObservadores(EstadoPedido estadoAnterior) {
        for (PedidoObserver observador : observadores) {
            observador.actualizar(this, estadoAnterior);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido pedido)) return false;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Pedido #%d [Estado: %s, Total: %s, Ítems: %d]",
                id, estado, MonedaUtils.formatear(calcularTotal()), items.size());
    }
}
