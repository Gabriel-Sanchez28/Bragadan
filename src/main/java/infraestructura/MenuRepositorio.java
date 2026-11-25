package infraestructura;

import dominio.ConfigProducto;

import java.util.List;
import java.util.Optional;

public interface MenuRepositorio {
    void guardar(ConfigProducto config);
    Optional<ConfigProducto> buscarPorCodigo(String codigo);
    List<ConfigProducto> listarTodos();
    boolean existePorCodigo(String codigo);
    void eliminar(String codigo);
}
