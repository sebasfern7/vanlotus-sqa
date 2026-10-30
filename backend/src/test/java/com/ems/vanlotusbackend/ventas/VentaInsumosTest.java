package com.ems.vanlotusbackend.ventas;

import com.ems.vanlotusbackend.model.ComponenteProducto;
import com.ems.vanlotusbackend.model.Insumo;
import com.ems.vanlotusbackend.model.MovimientoInsumo;
import com.ems.vanlotusbackend.model.ProductoVariante;
import com.ems.vanlotusbackend.ventas.support.VentaFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HU-03: Vender solo si hay stock suficiente — también de los insumos de la receta.
 * Cada joya consume insumos (receta / BOM). Técnica: valores límite sobre stock del insumo vs. cantidad requerida.
 */
@DisplayName("HU-03 Stock de insumos de la receta")
class VentaInsumosTest {

    private final VentaFixture fx = new VentaFixture();
    private ProductoVariante anillo;

    @BeforeEach
    void recetaDeUnAnillo() {
        anillo = fx.variante(1, "150.00", 10);

        Insumo plata = new Insumo();
        plata.setId(50);
        plata.setNombre("Plata 925 (g)");
        plata.setStockActual(new BigDecimal("6.0"));
        when(fx.insumoRepository.findById(50)).thenReturn(Optional.of(plata));

        ComponenteProducto receta = new ComponenteProducto();
        receta.setVariante(anillo);
        receta.setInsumo(plata);
        receta.setCantidadUtilizada(new BigDecimal("3.0"));
        when(fx.componenteRepository.findByVarianteId(1)).thenReturn(List.of(receta));
        lenient().when(fx.movimientoInsumoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Valor límite: 2 anillos usan 6 g de plata y hay 6 g: se vende y la plata queda en 0")
    void insumoJustoAlcanza() {
        fx.ventaService.createVenta(fx.pedido(anillo, 2, "150.00", "300.00"));

        ArgumentCaptor<MovimientoInsumo> captor = ArgumentCaptor.forClass(MovimientoInsumo.class);
        verify(fx.movimientoInsumoRepository).save(captor.capture());
        assertThat(captor.getValue().getTipoMovimiento()).isEqualTo("SALIDA");
        assertThat(captor.getValue().getCantidad()).isEqualByComparingTo("6.0");
        assertThat(captor.getValue().getStockResultante()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Valor límite: 3 anillos necesitan 9 g y hay 6 g: la venta se rechaza")
    void insumoNoAlcanza() {
        assertThatThrownBy(() -> fx.ventaService.createVenta(fx.pedido(anillo, 3, "150.00", "450.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Stock de insumo insuficiente: Plata 925 (g)");

        verify(fx.movimientoInsumoRepository, never()).save(any());
    }
}
