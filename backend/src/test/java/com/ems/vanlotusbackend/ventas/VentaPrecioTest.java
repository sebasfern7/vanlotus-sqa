package com.ems.vanlotusbackend.ventas;

import com.ems.vanlotusbackend.model.ProductoVariante;
import com.ems.vanlotusbackend.ventas.support.VentaFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HU-02: Registrar venta con el precio correcto.
 * Técnica: partición de equivalencia (precio de catálogo = clase válida; precio distinto = clase inválida).
 * Riesgo detectado en la revisión de código: el back guardaba el precio y el subtotal que enviaba el front.
 */
@DisplayName("HU-02 Registrar venta con el precio correcto")
class VentaPrecioTest {

    private final VentaFixture fx = new VentaFixture();

    @Test
    @DisplayName("CT-05: un precio unitario distinto al de catálogo se rechaza y no se guarda la venta")
    void precioDistintoAlDeCatalogoSeRechaza() {
        ProductoVariante anillo = fx.variante(1, "150.00", 10);

        assertThatThrownBy(() -> fx.ventaService.createVenta(fx.pedido(anillo, 2, "15.00", "30.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio de catálogo");

        assertThat(fx.detallesGuardados()).isEmpty();
        assertThat(fx.movimientosRegistrados()).isEmpty();
    }

    @Test
    @DisplayName("Un subtotal que no es precio x cantidad se rechaza")
    void subtotalInconsistenteSeRechaza() {
        ProductoVariante anillo = fx.variante(1, "150.00", 10);

        assertThatThrownBy(() -> fx.ventaService.createVenta(fx.pedido(anillo, 2, "150.00", "100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("subtotal");

        assertThat(fx.detallesGuardados()).isEmpty();
    }
}
