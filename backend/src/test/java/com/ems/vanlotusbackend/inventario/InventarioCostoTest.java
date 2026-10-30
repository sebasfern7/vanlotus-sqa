package com.ems.vanlotusbackend.inventario;

import com.ems.vanlotusbackend.model.HistorialCostosInsumo;
import com.ems.vanlotusbackend.model.Insumo;
import com.ems.vanlotusbackend.model.MovimientoInsumo;
import com.ems.vanlotusbackend.model.Usuario;
import com.ems.vanlotusbackend.repository.*;
import com.ems.vanlotusbackend.service.InventarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HU-06: Registrar una compra de insumos.
 * Al comprar, el costo unitario del insumo se recalcula por promedio ponderado (PMP),
 * el cambio queda en el historial de costos y el stock sube con un movimiento de ENTRADA.
 */
@DisplayName("HU-06 Registrar una compra de insumos")
class InventarioCostoTest {

    private final MovimientoInsumoRepository movimientoInsumoRepository = mock(MovimientoInsumoRepository.class);
    private final HistorialCostosInsumoRepository historialRepository = mock(HistorialCostosInsumoRepository.class);
    private final InventarioService inventario = new InventarioService(
            movimientoInsumoRepository, mock(MovimientoProductoRepository.class), historialRepository,
            mock(InsumoRepository.class), mock(ProductoVarianteRepository.class));

    private static BigDecimal bs(String valor) {
        return new BigDecimal(valor);
    }

    @Test
    @DisplayName("CT-14: 10 unidades a Bs 5 + compra de 10 a Bs 7 = costo promedio de Bs 6")
    void costoPromedioPonderado() {
        BigDecimal costo = inventario.calcularPromedioPonderado(bs("10"), bs("5.00"), bs("10"), bs("7.00"));

        assertThat(costo).isEqualByComparingTo("6.00");
    }

    @Test
    @DisplayName("Sin stock previo, el costo nuevo es el de la compra")
    void sinStockPrevioElCostoEsElDeLaCompra() {
        BigDecimal costo = inventario.calcularPromedioPonderado(bs("0"), bs("5.00"), bs("4"), bs("8.00"));

        assertThat(costo).isEqualByComparingTo("8.00");
    }

    @Test
    @DisplayName("Valor límite: stock total 0 no divide entre cero y devuelve el costo de la compra")
    void stockTotalCeroNoDivideEntreCero() {
        BigDecimal costo = inventario.calcularPromedioPonderado(bs("0"), bs("5.00"), bs("0"), bs("8.00"));

        assertThat(costo).isEqualByComparingTo("8.00");
    }

    @Test
    @DisplayName("El costo se redondea a 2 decimales hacia arriba en la mitad (1.005 -> 1.01)")
    void redondeoADosDecimales() {
        BigDecimal costo = inventario.calcularPromedioPonderado(bs("3"), bs("1.00"), bs("3"), bs("1.01"));

        assertThat(costo).isEqualByComparingTo("1.01");
    }

    @Test
    @DisplayName("Si el costo cambia, queda registrado en el historial con el valor anterior y el nuevo")
    void cambioDeCostoQuedaEnElHistorial() {
        inventario.registrarHistorialCosto(new Insumo(), new Usuario(), bs("5.00"), bs("6.00"), 12, "Compra #12");

        ArgumentCaptor<HistorialCostosInsumo> captor = ArgumentCaptor.forClass(HistorialCostosInsumo.class);
        verify(historialRepository).save(captor.capture());
        assertThat(captor.getValue().getCostoAnterior()).isEqualByComparingTo("5.00");
        assertThat(captor.getValue().getCostoNuevo()).isEqualByComparingTo("6.00");
        assertThat(captor.getValue().getCompraId()).isEqualTo(12);
    }

    @Test
    @DisplayName("Si el costo no cambia, no se ensucia el historial")
    void costoIgualNoSeRegistra() {
        inventario.registrarHistorialCosto(new Insumo(), new Usuario(), bs("5.00"), bs("5.00"), 12, "Compra #12");

        verify(historialRepository, never()).save(any());
    }

    @Test
    @DisplayName("Una compra registra una ENTRADA y el stock resultante sube")
    void compraSubeElStock() {
        when(movimientoInsumoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Insumo plata = new Insumo();
        plata.setStockActual(bs("5"));

        MovimientoInsumo entrada = inventario.registrarMovimientoInsumo(
                plata, new Usuario(), "ENTRADA", bs("10"), "COMPRA", 12, "Compra #12");

        assertThat(entrada.getTipoMovimiento()).isEqualTo("ENTRADA");
        assertThat(entrada.getStockResultante()).isEqualByComparingTo("15");
    }
}
