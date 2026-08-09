package com.eComm.eComm.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name ="tbl_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String itemId;

    private String name;
    private BigDecimal price;
    private String description;

    // ✅ NEW: Add stock/inventory fields
    @Column(nullable = false)//4/19/2026
    @Builder.Default
    private Integer stockQuantity = 0; // Current available stock

    @Column(nullable = false)//4/19/2026
    @Builder.Default
    private Integer lowStockThreshold = 10; // Alert when stock is low

    @Column(nullable = false)//4/19/2026
    @Builder.Default
    private Boolean inStock = true; // Quick check if item is available

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private CategoryEntity category;

    // ✅ Helper method to update stock status
    @PreUpdate
    public void updateStockStatus() {//4/19/2026
        this.inStock = this.stockQuantity > 0;
    }

}
