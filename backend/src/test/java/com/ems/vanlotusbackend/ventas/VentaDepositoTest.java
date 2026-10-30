package com.ems.vanlotusbackend.ventas;

import com.ems.vanlotusbackend.model.ProductoVariante;
import com.ems.vanlotusbackend.model.Venta;
import com.ems.vanlotusbackend.ventas.support.VentaFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HU-02: Registrar venta con el precio correcto — el depósito del pedido.
 * Regla: si no se indica, el depósito es el 50 % del total (redondeado a 2 decimales).
 */
@DisplayName("HU-02 Depósito del pedido")
class VentaDepositoTest {

    private final VentaFixture fx = new VentaFixture();
    private final ProductoVariante anillo = fx.variante(1, "150.00", 10);

    @Test
    @DisplayName("Sin porcentaje indicado, el depósito es el 50 % del total")
    void depositoPorDefectoEsLaMitad() {
        var pedido = fx.pedido(anillo, 2, "150.00", "300.00");
        pedido.setCostoEnvio(new BigDecimal("20.00"));

        Venta venta = fx.ventaService.createVenta(pedido);

        assertThat(venta.getPorcentajeDeposito()).isEqualByComparingTo("50.00");
        assertThat(venta.getDeposito()).isEqualByComparingTo("160.00");
    }

    @Test
    @DisplayName("Con un porcentaje del 30 %, el depósito es el 30 % del total")
    void depositoConPorcentaje() {
        var pedido = fx.pedido(anillo, 2, "150.00", "300.00");
        pedido.setPorcentajeDeposito(new BigDecimal("30"));

        Venta venta = fx.ventaService.createVenta(pedido);

        assertThat(venta.getDeposito()).isEqualByComparingTo("90.00");
    }

    @Test
    @DisplayName("Si la vendedora indica un depósito, se respeta ese monto")
    void depositoIndicadoSeRespeta() {
        var pedido = fx.pedido(anillo, 1, "150.00", "150.00");
        pedido.setDeposito(new BigDecimal("100.00"));

        Venta venta = fx.ventaService.createVenta(pedido);

        assertThat(venta.getDeposito()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Valor límite: el redondeo del depósito es a 2 decimales (33.335 -> 33.34)")
    void redondeoDelDeposito() {
        ProductoVariante arete = fx.variante(2, "66.67", 10);

        Venta venta = fx.ventaService.createVenta(fx.pedido(arete, 1, "66.67", "66.67"));

        assertThat(venta.getDeposito()).isEqualByComparingTo("33.34");
    }
}
