package com.ems.vanlotusbackend.ventas;

import com.ems.vanlotusbackend.model.MovimientoProducto;
import com.ems.vanlotusbackend.model.ProductoVariante;
import com.ems.vanlotusbackend.model.Venta;
import com.ems.vanlotusbackend.ventas.support.VentaFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HU-04: Entregar o cancelar una venta.
 * Técnica: transición de estados. CONFIRMADO -> ENTREGADO | CANCELADO; ENTREGADO y CANCELADO son finales.
 */
@DisplayName("HU-04 Entregar o cancelar una venta")
class VentaEstadosTest {

    private final VentaFixture fx = new VentaFixture();
    private final ProductoVariante anillo = fx.variante(1, "150.00", 5);

    @Test
    @DisplayName("CT-08: entregar una venta confirmada la deja ENTREGADO y el depósito pasa a ser el total")
    void entregarUnaVentaConfirmada() {
        fx.ventaGuardada(7, "CONFIRMADO", "320.00", anillo, 2);

        Venta venta = fx.ventaService.entregar(7);

        assertThat(venta.getEstado()).isEqualTo("ENTREGADO");
        assertThat(venta.getFechaEntrega()).isNotNull();
        assertThat(venta.getDeposito()).isEqualByComparingTo("320.00");
    }

    @Test
    @DisplayName("CT-09: entregar una venta cancelada se rechaza")
    void entregarUnaVentaCancelada() {
        fx.ventaGuardada(7, "CANCELADO", "320.00", anillo, 2);

        assertThatThrownBy(() -> fx.ventaService.entregar(7))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se puede entregar una venta cancelada");
    }

    @Test
    @DisplayName("Entregar dos veces la misma venta se rechaza")
    void entregarUnaVentaYaEntregada() {
        fx.ventaGuardada(7, "ENTREGADO", "320.00", anillo, 2);

        assertThatThrownBy(() -> fx.ventaService.entregar(7))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La venta ya fue entregada");
    }

    @Test
    @DisplayName("CT-10: cancelar una venta entregada se rechaza")
    void cancelarUnaVentaEntregada() {
        fx.ventaGuardada(7, "ENTREGADO", "320.00", anillo, 2);

        assertThatThrownBy(() -> fx.ventaService.cancelar(7))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se puede cancelar una venta ya entregada");
        assertThat(fx.movimientosRegistrados()).isEmpty();
    }

    @Test
    @DisplayName("CT-11: cancelar una venta confirmada la deja CANCELADO y devuelve el stock")
    void cancelarDevuelveElStock() {
        fx.ventaGuardada(7, "CONFIRMADO", "320.00", anillo, 2);

        Venta venta = fx.ventaService.cancelar(7);

        assertThat(venta.getEstado()).isEqualTo("CANCELADO");
        MovimientoProducto entrada = fx.movimientosRegistrados().get(0);
        assertThat(entrada.getTipoMovimiento()).isEqualTo("ENTRADA");
        assertThat(entrada.getCantidad()).isEqualTo(2);
        assertThat(entrada.getStockResultante()).isEqualTo(7);
    }
}
