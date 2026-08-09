package com.eComm.eComm.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemResponse
{
    private String itemId;
    private String name;
    private BigDecimal price;
    private String categoryId;
    private String description;
    private String categoryName;
    private String imgUrl;

    // ✅ NEW: Stock information//4/19/2026
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private Boolean inStock;
    private Boolean isLowStock; // Computed field//4/19/2026

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
