package com.eComm.eComm.io;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest
{
    private String name;
    private BigDecimal price;
    private String categoryId;
    private String description;

    // ✅ NEW: Stock quantity (optional, defaults to 0)
    @PositiveOrZero(message = "Stock quantity cannot be negative")//4/19/2026
    @Builder.Default
    private Integer stockQuantity = 0;

    @PositiveOrZero(message = "Low stock threshold cannot be negative")//4/19/2026
    @Builder.Default
    private Integer lowStockThreshold = 10;
}
