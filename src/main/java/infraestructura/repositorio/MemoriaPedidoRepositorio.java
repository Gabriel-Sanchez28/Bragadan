package infraestructura.repositorio;


import dominio.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//Implementación en memoria thread-safe del repositorio de pedidos de Bragadan.
public class MemoriaPedidoRepositorio implements PedidoRepositorio {

    private final Map<Long, Pedido> pedidos = new ConcurrentHashMap<>();
    private final AtomicLong contadorId = new AtomicLong(1);

    @Override
    public Pedido guardar(Pedido pedido) {
        Objects.requireNonNull(pedido, "El pedido no puede ser nulo");
        pedidos.put(pedido.getId(), pedido);
        return pedido;
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public List<Pedido> listarTodos() {
        return List.copyOf(new ArrayList<>(pedidos.values()));
    }

    @Override
    public List<Pedido> listarActivos() {
        return pedidos.values().stream()
                .filter(p -> !p.getEstado().esTerminal())
                .toList();
    }

    @Override
    public Long generarSiguienteId() {
        return contadorId.getAndIncrement();
    }
}
