package infraestructura.config;

import dominio.ConfigProducto;
import infraestructura.repositorio.MenuRepositorio;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

//Cargador de la configuración inicial del menú para la cafetería Bragadan.
public final class MenuConfigLoader {

    private MenuConfigLoader() {
    }

    //Carga el catálogo inicial de productos de Bragadan en el repositorio provisto.
    public static void cargarMenuInicial(MenuRepositorio repositorio) {
        Objects.requireNonNull(repositorio, "El repositorio no puede ser nulo");

        //    BEBIDAS
        repositorio.guardar(new ConfigProducto(
                "BEB-01", "Espresso Bragadan",
                "Café espresso intenso de especialidad con notas achocolatadas",
                new BigDecimal("4500.00"), TipoProducto.BEBIDA,
                Map.of("temperatura", TemperaturaBebida.CALIENTE)
        ));

        repositorio.guardar(new ConfigProducto(
                "BEB-02", "Café Americano",
                "Doble shot de espresso con agua filtrada a temperatura perfecta",
                new BigDecimal("5000.00"), TipoProducto.BEBIDA,
                Map.of("temperatura", TemperaturaBebida.CALIENTE)
        ));

        repositorio.guardar(new ConfigProducto(
                "BEB-03", "Cappuccino Cremoso",
                "Espresso, leche vaporizada y sedosa espuma de leche artesanal",
                new BigDecimal("7500.00"), TipoProducto.BEBIDA,
                Map.of("temperatura", TemperaturaBebida.CALIENTE)
        ));

        repositorio.guardar(new ConfigProducto(
                "BEB-04", "Cold Brew Bragadan",
                "Extracción lenta en frío durante 18 horas, refrescante y bajo en acidez",
                new BigDecimal("8000.00"), TipoProducto.BEBIDA,
                Map.of("temperatura", TemperaturaBebida.FRIA)
        ));

        repositorio.guardar(new ConfigProducto(
                "BEB-05", "Jugo de Naranja Natural",
                "Zumo recién exprimido 100% natural sin azúcar añadida",
                new BigDecimal("6000.00"), TipoProducto.BEBIDA,
                Map.of("temperatura", TemperaturaBebida.FRIA)
        ));

        //     PLATOS
        repositorio.guardar(new ConfigProducto(
                "PLA-01", "Croissant Horneado",
                "Croissant hojaldrado artesanal relleno de jamón selecto y queso fundido",
                new BigDecimal("9500.00"), TipoProducto.PLATO,
                Map.of("categoria", CategoriaPlato.DESAYUNO)
        ));

        repositorio.guardar(new ConfigProducto(
                "PLA-02", "Sándwich Gourmet Bragadan",
                "Pan masa madre, pechuga a la plancha, rúgula, queso provolone y pesto",
                new BigDecimal("16000.00"), TipoProducto.PLATO,
                Map.of("categoria", CategoriaPlato.ALMUERZO)
        ));

        repositorio.guardar(new ConfigProducto(
                "PLA-03", "Tostada Francesa Clásica",
                "Pan brioche dorado a la mantequilla con miel de maple y frutos del bosque",
                new BigDecimal("12000.00"), TipoProducto.PLATO,
                Map.of("categoria", CategoriaPlato.DESAYUNO)
        ));

        repositorio.guardar(new ConfigProducto(
                "PLA-04", "Quiche de Espinaca y Ricotta",
                "Tartaleta salada con masa crocante y relleno suave de espinacas",
                new BigDecimal("11000.00"), TipoProducto.PLATO,
                Map.of("categoria", CategoriaPlato.SNACK)
        ));

        //    POSTRES
        repositorio.guardar(new ConfigProducto(
                "POS-01", "Torta de Zanahoria",
                "Clásica torta húmeda con canela, nueces y glaseado suave de queso crema",
                new BigDecimal("8500.00"), TipoProducto.POSTRE,
                Map.of("libreDeGluten", false)
        ));

        repositorio.guardar(new ConfigProducto(
                "POS-02", "Brownie de Chocolate",
                "Brownie melcochudo elaborado con cacao fino de aroma al 70%",
                new BigDecimal("7000.00"), TipoProducto.POSTRE,
                Map.of("libreDeGluten", false)
        ));

        repositorio.guardar(new ConfigProducto(
                "POS-03", "Galleta Avena y Frutos Rojos",
                "Galleta crujiente artesanal horneada sin gluten",
                new BigDecimal("5500.00"), TipoProducto.POSTRE,
                Map.of("libreDeGluten", true)
        ));
    }
}
