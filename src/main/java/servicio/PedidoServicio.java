package servicio;

import dominio.Caja;
import dominio.Cocina;
import dominio.Mesero;
import dominio.PedidoObserver;
import infraestructura.PedidoRepositorio;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class PedidoServicio {
    private final PedidoRepositorio pedidoRepositorio;
    private final MenuServicio menuServicio;
    private final List<Supplier<PedidoObserver>> proveedoresObservadores;

    public PedidoServicio(PedidoRepositorio pedidoRepositorio, MenuServicio menuServicio,
                          List<Supplier<PedidoObserver>> proveedoresObservadores) {
        this.pedidoRepositorio = Objects.requireNonNull(pedidoRepositorio, "El repositorio de pedidos no puede ser nulo");
        this.menuServicio = Objects.requireNonNull(menuServicio, "El servicio de menú no puede ser nulo");
        this.proveedoresObservadores = proveedoresObservadores != null
                ? List.copyOf(proveedoresObservadores)
                : List.of(Cocina::new, Caja::new, Mesero::new);
    }
}
