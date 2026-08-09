package com.eComm.eComm.Entity;


import com.eComm.eComm.io.PaymentDetails;
import com.eComm.eComm.io.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
* this Entity Class contain customer Order Details orderI name number total Rs what he buy and tax
* also when customer buy something it gat link table for category in that item so we have to set the
* relation between this table
* we have to add One-TO-Many relation for  <orderItem entity>
* */

@Entity
@Table(name="tbl_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEnitity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String orderId;
    private String customerName;
    private String phoneNumber;
    private Double subtotal;
    private Double tax;
    private Double grandTotal;
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL , orphanRemoval = true)
    @JoinColumn(name="Order_id")
    private List<com.eComm.eComm.Entity.OrderItemEntity> items = new ArrayList<>();

    @Embedded
    private PaymentDetails paymentDetails;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;


    @PrePersist
    protected void onCreate(){
       this.orderId ="ORD"+System.currentTimeMillis();
       this.createdAt = LocalDateTime.now();
    }
}
