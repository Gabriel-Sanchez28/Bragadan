package infraestructura.repositorio;

import dominio.ConfigProducto;

import java.util.List;
import java.util.Optional;

//repositorio para el catalogo del menu
public interface MenuRepositorio {
    //guarda o actualiza la configuracion de un producto
    void guardar(ConfigProducto config);

    //busca la configuracion de un producto mediante un codigo
    Optional<ConfigProducto> buscarPorCodigo(String codigo);
    //lista todas las configuraciones
    List<ConfigProducto> listarTodos();

    //verificacion de si existe un producto registrado
    boolean existePorCodigo(String codigo);

    //elimina un producto
    void eliminar(String codigo);
}
