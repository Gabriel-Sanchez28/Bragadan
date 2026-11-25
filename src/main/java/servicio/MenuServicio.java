package servicio;

import dominio.ProductoFactoryRegistry;
import infraestructura.MenuRepositorio;

import java.util.Objects;

public class MenuServicio {
    private final MenuRepositorio menuRepositorio;
    private final ProductoFactoryRegistry factoryRegistry;

    public MenuServicio(MenuRepositorio menuRepositorio, ProductoFactoryRegistry factoryRegistry) {
        this.menuRepositorio = Objects.requireNonNull(menuRepositorio, "El repositorio del menú no puede ser nulo");
        this.factoryRegistry = Objects.requireNonNull(factoryRegistry, "El registro de fábricas no puede ser nulo");
    }
}
