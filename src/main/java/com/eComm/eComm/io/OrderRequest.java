package com.eComm.eComm.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // <-- 1.THIS SAFETY NET
public class OrderRequest {

    private String customerName;
    private String phoneNumber;
    private List<OrderItemRequest> cartItems;
    private Double subtotal;
    private Double tax;
    private Double grandTotal;
    private String paymentMethod;


    /**
     * @ create a inner class
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true) // <-- 2. THIS SAFETY NET
    public static class OrderItemRequest {

        // 3. THIS ANNOTATION: It tells Jackson to accept "itemid" from JSON
        @JsonProperty("itemid")
          private String itemId;
          private String name;
          private Double price;
          private Integer quantity;

    }
}
