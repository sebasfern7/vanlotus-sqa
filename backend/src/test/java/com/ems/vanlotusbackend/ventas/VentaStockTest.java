package com.ems.vanlotusbackend.ventas;

import com.ems.vanlotusbackend.model.MovimientoProducto;
import com.ems.vanlotusbackend.model.ProductoVariante;
import com.ems.vanlotusbackend.model.Venta;
import com.ems.vanlotusbackend.ventas.support.VentaFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HU-03: Vender solo si hay stock suficiente.
 * Técnica: análisis de valores límite sobre la condición stockActual < cantidad.
 */
@DisplayName("HU-03 Vender solo si hay stock suficiente")
class VentaStockTest {

    private final VentaFixture fx = new VentaFixture();

    @Test
    @DisplayName("CT-06: vender exactamente el stock disponible (3 de 3) se acepta y el stock queda en 0")
    void venderExactamenteElStock() {
        ProductoVariante anillo = fx.variante(1, "150.00", 3);

        Venta venta = fx.ventaService.createVenta(fx.pedido(anillo, 3, "150.00", "450.00"));

        assertThat(venta.getEstado()).isEqualTo("CONFIRMADO");
        MovimientoProducto salida = fx.movimientosRegistrados().get(0);
        assertThat(salida.getTipoMovimiento()).isEqualTo("SALIDA");
        assertThat(salida.getCantidad()).isEqualTo(3);
        assertThat(salida.getStockResultante()).isZero();
    }

    @Test
    @DisplayName("CT-07: vender una unidad más que el stock (4 de 3) se rechaza y no se mueve inventario")
    void venderUnaUnidadMasQueElStock() {
        ProductoVariante anillo = fx.variante(1, "150.00", 3);

        assertThatThrownBy(() -> fx.ventaService.createVenta(fx.pedido(anillo, 4, "150.00", "600.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuficiente");

        assertThat(fx.movimientosRegistrados()).isEmpty();
        assertThat(fx.detallesGuardados()).isEmpty();
    }

    @Test
    @DisplayName("CT-04: venta con precio de catálogo: subtotal = precio x cantidad y total = subtotales + envío")
    void ventaConPrecioDeCatalogo() {
        ProductoVariante anillo = fx.variante(1, "150.00", 10);
        var pedido = fx.pedido(anillo, 2, "150.00", "300.00");
        pedido.setCostoEnvio(new BigDecimal("20.00"));

        Venta venta = fx.ventaService.createVenta(pedido);

        assertThat(venta.getTotal()).isEqualByComparingTo("320.00");
        assertThat(fx.detallesGuardados().get(0).getPrecioUnitario()).isEqualByComparingTo("150.00");
        assertThat(fx.detallesGuardados().get(0).getSubtotal()).isEqualByComparingTo("300.00");
    }
}
