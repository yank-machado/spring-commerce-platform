package com.marketplace.salesapi.order.controller;

import com.marketplace.salesapi.order.dto.OrderSummaryDto;
import com.marketplace.salesapi.order.dto.ProductSalesDto;
import com.marketplace.salesapi.order.dto.StoreSalesDto;
import com.marketplace.salesapi.order.service.OrderAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Análise de Pedidos", description = "API para análise de dados de pedidos")
public class OrderAnalyticsController {

    @Autowired
    private OrderAnalyticsService analyticsService;

    @GetMapping("/orders/summary")
    @Operation(summary = "Obter resumo de pedidos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderSummaryDto> getOrderSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        OrderSummaryDto summary = analyticsService.getOrderSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/stores/{storeId}/summary")
    @Operation(summary = "Obter resumo de pedidos de uma loja")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<OrderSummaryDto> getStoreSummary(
            @PathVariable Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        OrderSummaryDto summary = analyticsService.getStoreSummary(storeId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/stores/top-selling")
    @Operation(summary = "Obter lojas com mais vendas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StoreSalesDto>> getTopSellingStores(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<StoreSalesDto> topStores = analyticsService.getTopSellingStores(limit, startDate, endDate);
        return ResponseEntity.ok(topStores);
    }

    @GetMapping("/products/top-selling")
    @Operation(summary = "Obter produtos mais vendidos")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProductSalesDto>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<ProductSalesDto> topProducts = analyticsService.getTopSellingProducts(limit, startDate, endDate);
        return ResponseEntity.ok(topProducts);
    }
}