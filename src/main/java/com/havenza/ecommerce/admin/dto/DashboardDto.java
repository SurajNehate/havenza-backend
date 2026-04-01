package com.havenza.ecommerce.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DashboardDto {
    private long totalUsers;
    private long totalOrders;
    private long totalProducts;
    private BigDecimal totalRevenue;
    private Map<String, Long> salesByStatus;
    private Map<String, BigDecimal> monthlySales;
}
