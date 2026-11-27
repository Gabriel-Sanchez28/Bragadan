package dto;

import comun.MonedaUtils;
import dominio.modelo.Pedido;
import dominio.modelo.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mapper para convertir entre la entidad de dominio Pedido y sus DTOs correspondientes.
 */
public final class ProductoMapper {

    private ProductoMapper() {
    }

    public static ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        return new ProductoDTO(
                producto.getCodigo(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                MonedaUtils.formatear(producto.getPrecio()),
                producto.getTipo() != null ? producto.getTipo().name() : "DESCONOCIDO"
        );
    }

    public static List<ProductoDTO> toDTOList(List<Producto> productos) {
        if (productos == null) {
            return Collections.emptyList();
        }
        return productos.stream()
                .map(ProductoMapper::toDTO)
                .toList();
    }

    public static ConfigProducto toConfig(ConfigProductoDTO dto) {
        if (dto == null) {
            return null;
        }
        TipoProducto tipo = dto.tipo() != null
                ? TipoProducto.valueOf(dto.tipo().trim().toUpperCase())
                : TipoProducto.BEBIDA;

        return new ConfigProducto(
                dto.codigo(),
                dto.nombre(),
                dto.descripcion(),
                dto.precioBase(),
                tipo,
                dto.atributos()
        );
    }

    public static ConfigProductoDTO toConfigDTO(ConfigProducto config) {
        if (config == null) {
            return null;
        }
        return new ConfigProductoDTO(
                config.getCodigo(),
                config.getNombre(),
                config.getDescripcion(),
                config.getPrecioBase(),
                config.getTipo().name(),
                config.getAtributos()
        );
    }
}
