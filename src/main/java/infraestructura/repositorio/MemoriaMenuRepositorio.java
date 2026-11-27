package infraestructura.repositorio;

import dominio.factory.ConfigProducto;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//Implementación en memoria thread-safe del repositorio de pedidos de Bragadan.
public class MemoriaMenuRepositorio implements MenuRepositorio {

    private final Map<String, ConfigProducto> catalogo = new ConcurrentHashMap<>();

    @Override
    public void guardar(ConfigProducto config) {
        Objects.requireNonNull(config, "La configuración del producto no puede ser nula");
        // Se guarda un clon para garantizar aislamiento y evitar mutaciones externas inesperadas
        catalogo.put(config.getCodigo(), config.clonar());
    }

    @Override
    public Optional<ConfigProducto> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.empty();
        }
        ConfigProducto config = catalogo.get(codigo.trim());
        return Optional.ofNullable(config != null ? config.clonar() : null);
    }

    @Override
    public List<ConfigProducto> listarTodos() {
        List<ConfigProducto> lista = new ArrayList<>();
        for (ConfigProducto c : catalogo.values()) {
            lista.add(c.clonar());
        }
        return List.copyOf(lista);
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return false;
        }
        return catalogo.containsKey(codigo.trim());
    }

    @Override
    public void eliminar(String codigo) {
        if (codigo != null) {
            catalogo.remove(codigo.trim());
        }
    }
}