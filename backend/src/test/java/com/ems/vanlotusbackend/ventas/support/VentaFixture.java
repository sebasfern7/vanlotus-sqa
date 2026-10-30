package com.ems.vanlotusbackend.ventas.support;

import com.ems.vanlotusbackend.dto.ventas.DetalleVentaRequest;
import com.ems.vanlotusbackend.dto.ventas.VentaRequest;
import com.ems.vanlotusbackend.model.DetalleVenta;
import com.ems.vanlotusbackend.model.MovimientoProducto;
import com.ems.vanlotusbackend.model.ProductoVariante;
import com.ems.vanlotusbackend.model.Usuario;
import com.ems.vanlotusbackend.model.Venta;
import com.ems.vanlotusbackend.repository.*;
import com.ems.vanlotusbackend.security.CurrentUserService;
import com.ems.vanlotusbackend.service.InventarioService;
import com.ems.vanlotusbackend.service.VentaService;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Arma un VentaService con repositorios simulados (Mockito): las pruebas de ventas corren sin MySQL.
 * InventarioService es el real, para verificar el stock resultante de cada movimiento.
 */
public class VentaFixture {

    public final VentaRepository ventaRepository = mock(VentaRepository.class);
    public final DetalleVentaRepository detalleVentaRepository = mock(DetalleVentaRepository.class);
    public final ProductoVarianteRepository varianteRepository = mock(ProductoVarianteRepository.class);
    public final ComponenteProductoRepository componenteRepository = mock(ComponenteProductoRepository.class);
    public final MovimientoProductoRepository movimientoProductoRepository = mock(MovimientoProductoRepository.class);
    public final InsumoRepository insumoRepository = mock(InsumoRepository.class);
    public final MovimientoInsumoRepository movimientoInsumoRepository = mock(MovimientoInsumoRepository.class);
    private final CurrentUserService currentUserService = mock(CurrentUserService.class);

    public final VentaService ventaService;

    public VentaFixture() {
        InventarioService inventarioService = new InventarioService(
                movimientoInsumoRepository, movimientoProductoRepository,
                mock(HistorialCostosInsumoRepository.class), insumoRepository, varianteRepository);
        ventaService = new VentaService(
                ventaRepository, detalleVentaRepository, varianteRepository, mock(ClienteRepository.class),
                mock(PromocionRepository.class), componenteRepository, insumoRepository,
                mock(CampaniaMarketingRepository.class), inventarioService, currentUserService);

        lenient().when(currentUserService.getCurrentUser()).thenReturn(new Usuario());
        lenient().when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
            Venta venta = inv.getArgument(0);
            if (venta.getId() == null) {
                venta.setId(100);
            }
            return venta;
        });
        lenient().when(movimientoProductoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(componenteRepository.findByVarianteId(anyInt())).thenReturn(List.of());
    }

    /** Una variante del catálogo con su precio de venta y su stock actual. */
    public ProductoVariante variante(int id, String precioCatalogo, int stock) {
        ProductoVariante variante = new ProductoVariante();
        variante.setId(id);
        variante.setNombreVariante("Anillo plata talla " + id);
        variante.setPrecioVenta(new BigDecimal(precioCatalogo));
        variante.setStockActual(stock);
        lenient().when(varianteRepository.findById(id)).thenReturn(Optional.of(variante));
        return variante;
    }

    /** Una venta de una sola línea, con el precio unitario y el subtotal que envía el front. */
    public VentaRequest pedido(ProductoVariante variante, int cantidad, String precioUnitario, String subtotal) {
        DetalleVentaRequest detalle = new DetalleVentaRequest();
        detalle.setVarianteId(variante.getId());
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(new BigDecimal(precioUnitario));
        detalle.setSubtotal(new BigDecimal(subtotal));

        VentaRequest request = new VentaRequest();
        request.setCanalVenta("TIENDA");
        request.setMetodoPago("EFECTIVO");
        request.setDetalles(List.of(detalle));
        return request;
    }

    /** Una venta ya guardada en un estado dado, con una línea de la variante indicada. */
    public Venta ventaGuardada(int id, String estado, String total, ProductoVariante variante, int cantidad) {
        Venta venta = new Venta();
        venta.setId(id);
        venta.setEstado(estado);
        venta.setTotal(new BigDecimal(total));
        venta.setDeposito(BigDecimal.ZERO);
        lenient().when(ventaRepository.findById(id)).thenReturn(Optional.of(venta));

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setVariante(variante);
        detalle.setCantidad(cantidad);
        lenient().when(detalleVentaRepository.findByVentaId(id)).thenReturn(List.of(detalle));
        return venta;
    }

    /** Los movimientos de inventario que se registraron (SALIDA al vender, ENTRADA al cancelar). */
    public List<MovimientoProducto> movimientosRegistrados() {
        ArgumentCaptor<MovimientoProducto> captor = ArgumentCaptor.forClass(MovimientoProducto.class);
        verify(movimientoProductoRepository, atLeast(0)).save(captor.capture());
        return captor.getAllValues();
    }

    /** Las líneas de detalle que se guardaron con la venta. */
    @SuppressWarnings("unchecked")
    public List<DetalleVenta> detallesGuardados() {
        ArgumentCaptor<List<DetalleVenta>> captor = ArgumentCaptor.forClass(List.class);
        verify(detalleVentaRepository, atLeast(0)).saveAll(captor.capture());
        return captor.getAllValues().isEmpty() ? List.of() : captor.getValue();
    }
}
