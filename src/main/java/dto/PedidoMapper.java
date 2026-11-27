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
public final class PedidoMapper {

    private PedidoMapper() {
    }

    public static PedidoDTO toDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        List<ProductoDTO> itemsDTO = ProductoMapper.toDTOList(pedido.getItems());
        BigDecimal total = pedido.calcularTotal();

        return new PedidoDTO(
                pedido.getId(),
                pedido.getEstado().name(),
                itemsDTO,
                total,
                MonedaUtils.formatear(total),
                pedido.getFechaCreacion(),
                pedido.getItems().size()
        );
    }

    public static List<PedidoDTO> toDTOList(List<Pedido> pedidos) {
        if (pedidos == null) {
            return Collections.emptyList();
        }
        return pedidos.stream()
                .map(PedidoMapper::toDTO)
                .toList();
    }

    public static ResumenPedidoDTO toResumenDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        List<String> lineas = new ArrayList<>();
        List<Producto> items = pedido.getItems();

        for (int i = 0; i < items.size(); i++) {
            Producto item = items.get(i);
            lineas.add(String.format(" %d. %-30s %s (%s)",
                    (i + 1),
                    item.getNombre(),
                    MonedaUtils.formatear(item.getPrecio()),
                    item.getDescripcion()));
        }

        BigDecimal total = pedido.calcularTotal();

        return new ResumenPedidoDTO(
                pedido.getId(),
                pedido.getEstado().name(),
                lineas,
                total,
                MonedaUtils.formatear(total),
                LocalDateTime.now()
        );
    }
}
