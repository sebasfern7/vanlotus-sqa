package com.ems.vanlotusbackend.service;

import lombok.RequiredArgsConstructor;

import com.ems.vanlotusbackend.model.*;
import com.ems.vanlotusbackend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final MovimientoInsumoRepository movimientoInsumoRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final HistorialCostosInsumoRepository historialRepository;
    private final InsumoRepository insumoRepository;
    private final ProductoVarianteRepository varianteRepository;

    public BigDecimal calcularPromedioPonderado(BigDecimal stockActual, BigDecimal costoActual,
            BigDecimal cantidadCompra, BigDecimal costoCompra) {
        BigDecimal stockTotal = stockActual.add(cantidadCompra);
        if (stockTotal.compareTo(BigDecimal.ZERO) == 0) {
            return costoCompra;
        }
        BigDecimal valorActual = stockActual.multiply(costoActual);
        BigDecimal valorCompra = cantidadCompra.multiply(costoCompra);
        return valorActual.add(valorCompra).divide(stockTotal, 2, RoundingMode.HALF_UP);
    }

    public void registrarHistorialCosto(Insumo insumo, Usuario usuario, BigDecimal costoAnterior,
            BigDecimal costoNuevo, Integer compraId, String motivo) {
        if (costoAnterior.compareTo(costoNuevo) == 0) {
            return;
        }
        HistorialCostosInsumo historial = new HistorialCostosInsumo();
        historial.setInsumo(insumo);
        historial.setUsuario(usuario);
        historial.setCostoAnterior(costoAnterior);
        historial.setCostoNuevo(costoNuevo);
        historial.setCompraId(compraId);
        historial.setMotivo(motivo);
        historialRepository.save(historial);
    }

    public MovimientoInsumo registrarMovimientoInsumo(Insumo insumo, Usuario usuario, String tipo,
            BigDecimal cantidad, String referenciaTipo, Integer referenciaId, String observacion) {
        BigDecimal stockResultante = calcularStockResultanteInsumo(insumo.getStockActual(), tipo, cantidad);

        MovimientoInsumo movimiento = new MovimientoInsumo();
        movimiento.setInsumo(insumo);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockResultante(stockResultante);
        movimiento.setReferenciaTipo(referenciaTipo);
        movimiento.setReferenciaId(referenciaId);
        movimiento.setObservacion(observacion);

        MovimientoInsumo guardado = movimientoInsumoRepository.save(movimiento);
        insumoRepository.flush();
        return guardado;
    }

    public MovimientoProducto registrarMovimientoProducto(ProductoVariante variante, Usuario usuario,
            String tipo, Integer cantidad, String referenciaTipo, Integer referenciaId, String observacion) {
        Integer stockResultante = calcularStockResultanteProducto(variante.getStockActual(), tipo, cantidad);

        MovimientoProducto movimiento = new MovimientoProducto();
        movimiento.setVariante(variante);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setStockResultante(stockResultante);
        movimiento.setReferenciaTipo(referenciaTipo);
        movimiento.setReferenciaId(referenciaId);
        movimiento.setObservacion(observacion);

        MovimientoProducto guardado = movimientoProductoRepository.save(movimiento);
        varianteRepository.flush();
        return guardado;
    }

    private BigDecimal calcularStockResultanteInsumo(BigDecimal stockActual, String tipo, BigDecimal cantidad) {
        if ("SALIDA".equals(tipo)) {
            return stockActual.subtract(cantidad);
        }
        return stockActual.add(cantidad);
    }

    private Integer calcularStockResultanteProducto(Integer stockActual, String tipo, Integer cantidad) {
        if ("SALIDA".equals(tipo)) {
            return stockActual - cantidad;
        }
        return stockActual + cantidad;
    }
}
