package com.ems.vanlotusbackend.service;

import lombok.RequiredArgsConstructor;

import com.ems.vanlotusbackend.dto.ventas.DetalleVentaRequest;
import com.ems.vanlotusbackend.dto.ventas.VentaRequest;
import com.ems.vanlotusbackend.model.*;
import com.ems.vanlotusbackend.repository.*;
import com.ems.vanlotusbackend.security.CurrentUserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoVarianteRepository varianteRepository;
    private final ClienteRepository clienteRepository;
    private final PromocionRepository promocionRepository;
    private final ComponenteProductoRepository componenteRepository;
    private final InsumoRepository insumoRepository;
    private final CampaniaMarketingRepository campaniaRepository;
    private final InventarioService inventarioService;
    private final CurrentUserService currentUserService;

    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    public Venta findById(Integer id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + id));
    }

    @Transactional
    public Venta createVenta(VentaRequest request) {
        validarPrecios(request);

        Cliente cliente = null;
        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        }

        BigDecimal totalVenta = BigDecimal.ZERO;
        for (DetalleVentaRequest det : request.getDetalles()) {
            totalVenta = totalVenta.add(det.getSubtotal());
        }
        if (request.getCostoEnvio() != null) {
            totalVenta = totalVenta.add(request.getCostoEnvio());
        }

        BigDecimal porcentajeDeposito = request.getPorcentajeDeposito() != null
                ? request.getPorcentajeDeposito()
                : new BigDecimal("50.00");

        BigDecimal deposito = request.getDeposito();
        if (deposito == null) {
            deposito = totalVenta.multiply(porcentajeDeposito)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        Usuario usuario = currentUserService.getCurrentUser();

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setCliente(cliente);
        venta.setCanalVenta(request.getCanalVenta());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setCostoEnvio(request.getCostoEnvio() != null ? request.getCostoEnvio() : BigDecimal.ZERO);
        venta.setTotal(totalVenta);
        venta.setDeposito(deposito);
        venta.setPorcentajeDeposito(porcentajeDeposito);
        venta.setEstado("CONFIRMADO");
        venta.setFechaPedido(LocalDateTime.now());
        venta.setFechaPagoDeposito(LocalDateTime.now());

        if (request.getCampaniaId() != null) {
            CampaniaMarketing campania = campaniaRepository.findById(request.getCampaniaId())
                    .orElseThrow(() -> new IllegalArgumentException("Campaña no encontrada"));
            venta.setCampania(campania);
        }

        Venta ventaGuardada = ventaRepository.save(venta);
        List<DetalleVenta> detallesGuardar = new ArrayList<>();

        for (DetalleVentaRequest detRequest : request.getDetalles()) {
            ProductoVariante variante = varianteRepository.findById(detRequest.getVarianteId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Variante no encontrada: " + detRequest.getVarianteId()));

            if (variante.getStockActual() < detRequest.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para variante: " + variante.getNombreVariante());
            }

            variante = varianteRepository.findById(variante.getId()).orElseThrow();
            inventarioService.registrarMovimientoProducto(
                    variante, usuario, "SALIDA", detRequest.getCantidad(),
                    "VENTA", ventaGuardada.getId(), "Venta #" + ventaGuardada.getId());

            List<ComponenteProducto> componentes = componenteRepository.findByVarianteId(variante.getId());
            for (ComponenteProducto componente : componentes) {
                Insumo insumo = insumoRepository.findById(componente.getInsumo().getId()).orElseThrow();
                BigDecimal cantidadRequerida = componente.getCantidadUtilizada()
                        .multiply(BigDecimal.valueOf(detRequest.getCantidad()));

                if (insumo.getStockActual().compareTo(cantidadRequerida) < 0) {
                    throw new IllegalStateException("Stock de insumo insuficiente: " + insumo.getNombre());
                }

                inventarioService.registrarMovimientoInsumo(
                        insumo, usuario, "SALIDA", cantidadRequerida,
                        "VENTA", ventaGuardada.getId(), "Consumo por venta #" + ventaGuardada.getId());
            }

            Promocion promocion = null;
            if (detRequest.getPromocionId() != null) {
                promocion = promocionRepository.findById(detRequest.getPromocionId()).orElse(null);
            }

            DetalleVenta detalle = new DetalleVenta(
                    ventaGuardada,
                    variante,
                    promocion,
                    detRequest.getCantidad(),
                    detRequest.getPrecioUnitario(),
                    detRequest.getSubtotal());

            detallesGuardar.add(detalle);
        }

        detalleVentaRepository.saveAll(detallesGuardar);
        return ventaGuardada;
    }

    @Transactional
    public Venta entregar(Integer id) {
        Venta venta = findById(id);
        if ("CANCELADO".equals(venta.getEstado())) {
            throw new IllegalStateException("No se puede entregar una venta cancelada");
        }
        if ("ENTREGADO".equals(venta.getEstado())) {
            throw new IllegalStateException("La venta ya fue entregada");
        }
        venta.setEstado("ENTREGADO");
        venta.setFechaEntrega(LocalDateTime.now());
        venta.setDeposito(venta.getTotal());
        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta cancelar(Integer id) {
        Venta venta = findById(id);
        if ("ENTREGADO".equals(venta.getEstado())) {
            throw new IllegalStateException("No se puede cancelar una venta ya entregada");
        }
        if ("CANCELADO".equals(venta.getEstado())) {
            throw new IllegalStateException("La venta ya está cancelada");
        }

        Usuario usuario = currentUserService.getCurrentUser();
        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(id);

        for (DetalleVenta detalle : detalles) {
            ProductoVariante variante = varianteRepository.findById(detalle.getVariante().getId())
                    .orElseThrow();
            inventarioService.registrarMovimientoProducto(
                    variante, usuario, "ENTRADA", detalle.getCantidad(),
                    "VENTA_CANCEL", id, "Reversión por cancelación venta #" + id);

            List<ComponenteProducto> componentes = componenteRepository.findByVarianteId(variante.getId());
            for (ComponenteProducto componente : componentes) {
                Insumo insumo = insumoRepository.findById(componente.getInsumo().getId()).orElseThrow();
                BigDecimal cantidadRequerida = componente.getCantidadUtilizada()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()));
                inventarioService.registrarMovimientoInsumo(
                        insumo, usuario, "ENTRADA", cantidadRequerida,
                        "VENTA_CANCEL", id, "Reversión insumo por cancelación venta #" + id);
            }
        }

        venta.setEstado("CANCELADO");
        return ventaRepository.save(venta);
    }

    /**
     * HU-02: el precio lo define el catálogo, no el front. Se valida antes de guardar nada.
     * Las líneas con promoción quedan fuera de la comparación con el catálogo (el descuento lo cambia).
     */
    private void validarPrecios(VentaRequest request) {
        for (DetalleVentaRequest det : request.getDetalles()) {
            ProductoVariante variante = varianteRepository.findById(det.getVarianteId())
                    .orElseThrow(() -> new IllegalArgumentException("Variante no encontrada: " + det.getVarianteId()));

            if (det.getPromocionId() == null
                    && det.getPrecioUnitario().compareTo(variante.getPrecioVenta()) != 0) {
                throw new IllegalArgumentException("El precio unitario de " + variante.getNombreVariante()
                        + " no coincide con el precio de catálogo (" + variante.getPrecioVenta() + ")");
            }

            BigDecimal subtotalEsperado = det.getPrecioUnitario().multiply(BigDecimal.valueOf(det.getCantidad()));
            if (det.getSubtotal().compareTo(subtotalEsperado) != 0) {
                throw new IllegalArgumentException("El subtotal de " + variante.getNombreVariante()
                        + " debe ser precio x cantidad (" + subtotalEsperado + ")");
            }
        }
    }

    public List<DetalleVenta> getDetallesByVenta(Integer ventaId) {
        return detalleVentaRepository.findByVentaId(ventaId);
    }
}
