package infraestructura;

import dominio.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoRepositorio {
    //Guarda o actualiza un pedido en el almacén.
    Pedido guardar(Pedido pedido);

    //Busca un pedido por su identificador único.
    Optional<Pedido> buscarPorId(Long id);

    //Lista la totalidad de los pedidos registrados.
    List<Pedido> listarTodos();

    //Lista únicamente los pedidos activos (que no han llegado a un estado terminal como ENTREGADO o CANCELADO).
    List<Pedido> listarActivos();

    //Genera de forma atómica y segura el siguiente identificador de pedido.
    Long generarSiguienteId();
}
