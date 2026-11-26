package dominio.modelo;

import java.util.Set;

/**
 * Representa los estados del ciclo de vida de un pedido en Bragadan.
 * Define la máquina de estados y las transiciones válidas.
 */
public enum EstadoPedido {

    RECIBIDO {
        @Override
        public boolean puedePasarA(EstadoPedido nuevoEstado) {
            return nuevoEstado == EN_PREPARACION || nuevoEstado == CANCELADO;
        }
    },

    EN_PREPARACION {
        @Override
        public boolean puedePasarA(EstadoPedido nuevoEstado) {
            return nuevoEstado == LISTO || nuevoEstado == CANCELADO;
        }
    },

    LISTO {
        @Override
        public boolean puedePasarA(EstadoPedido nuevoEstado) {
            return nuevoEstado == ENTREGADO || nuevoEstado == CANCELADO;
        }
    },

    ENTREGADO {
        @Override
        public boolean puedePasarA(EstadoPedido nuevoEstado) {
            return false; // Estado final
        }
    },

    CANCELADO {
        @Override
        public boolean puedePasarA(EstadoPedido nuevoEstado) {
            return false; // Estado final
        }
    };

    /**
     * Valida si la transición hacia el nuevo estado es permitida por las reglas del negocio.
     *
     * @param nuevoEstado Estado al que se desea transicionar.
     * @return true si la transición es válida, false en caso contrario.
     */
    public abstract boolean puedePasarA(EstadoPedido nuevoEstado);

    /**
     * Indica si el estado actual es terminal (no permite más cambios).
     */
    public boolean esTerminal() {
        return this == ENTREGADO || this == CANCELADO;
    }

    /**
     * Retorna los estados a los que se puede transicionar desde el actual.
     */
    public Set<EstadoPedido> estadosSiguientesValidos() {
        return switch (this) {
            case RECIBIDO -> Set.of(EN_PREPARACION, CANCELADO);
            case EN_PREPARACION -> Set.of(LISTO, CANCELADO);
            case LISTO -> Set.of(ENTREGADO, CANCELADO);
            case ENTREGADO, CANCELADO -> Set.of();
        };
    }
}
