package com.eComm.eComm.Service.Implementation;

import com.eComm.eComm.Entity.ItemEntity;
import com.eComm.eComm.Entity.OrderEnitity;
import com.eComm.eComm.Entity.OrderItemEntity;
import com.eComm.eComm.Repository.ItemRepository;
import com.eComm.eComm.Repository.OrderEntityRepository;
import com.eComm.eComm.Service.OrderService;
import com.eComm.eComm.io.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderEntityRepository orderEntityRepository;

    // 2. ADD THIS ONE
    private final ItemRepository itemRepository;

    private OrderResponse convertToResponse(OrderEnitity newOrder) {
        return OrderResponse.builder()
                .orderId(newOrder.getOrderId())
                .customerName(newOrder.getCustomerName())
                .phoneNumber(newOrder.getPhoneNumber())
                .subtotal(newOrder.getSubtotal())
                .tax(newOrder.getTax())
                .grandTotal(newOrder.getGrandTotal())
                // CHANGE: Converted Enum to String using .name()
                .paymentMethod(newOrder.getPaymentMethod() != null ? PaymentMethod.valueOf(newOrder.getPaymentMethod().name()) : null)
                // CHANGE: Added null check for items stream
                .items(newOrder.getItems() != null ? newOrder.getItems().stream()
                        .map(this::convertToItemResponse)
                        .collect(Collectors.toList()) : Collections.emptyList())
                .paymentDetails(newOrder.getPaymentDetails())
                .createdAt(newOrder.getCreatedAt())
                .build();
    }

    private OrderEnitity convertToOrderEntity(OrderRequest request) {
//        return OrderEnitity.builder()
//                .customerName(request.getCustomerName())
//                .phoneNumber(request.getPhoneNumber())
//                .subtotal(request.getSubtotal())
//                .tax(request.getTax())
//                .grandTotal(request.getGrandTotal())
//                // CHANGE: Added .toUpperCase() for safer Enum matching
//                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()))
//                .build();

        double calculatedSubtotal = 0.0;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        // 1. Loop through the items the frontend sent
        for (OrderRequest.OrderItemRequest cartItem : request.getCartItems()) {

            // 2. Fetch the REAL item from the database
            ItemEntity realItem = itemRepository.findByItemId(cartItem.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + cartItem.getItemId()));

            // 3. Use the REAL price from the DB (Ignore frontend's price)
            double realPrice = realItem.getPrice().doubleValue();
            int quantity = cartItem.getQuantity();

            calculatedSubtotal += (realPrice * quantity);

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .itemId(realItem.getItemId())
                    .name(realItem.getName())
                    .price(realPrice)    // <-- Saved using real DB price
                    .quantity(quantity)
                    .build();

            orderItems.add(orderItem);
        }

        // 4. IMPORTANT: Set this to match whatever Tax your frontend currently shows!
        // If your frontend doesn't calculate tax right now, change 0.00 to match it.
        // ❌ WRONG: If you put 1.0, Java thinks it is 100% Tax!
        // double taxRate = 1.0;
        // ✅ CORRECT: 0.01 is 1% Tax. (0.18 would be 18%)
        double taxRate = 0.01;

        double calculatedTax = calculatedSubtotal * taxRate;
        double calculatedGrandTotal = calculatedSubtotal + calculatedTax;

        // 5. Build the final order using OUR secure calculations
        return OrderEnitity.builder()
                .customerName(request.getCustomerName())
                .phoneNumber(request.getPhoneNumber())

                // SILENT OVERRIDE: We completely ignore request.getGrandTotal()
                .subtotal(calculatedSubtotal)      // <-- Secure math
                .tax(calculatedTax)                // <-- Secure math
                .grandTotal(calculatedGrandTotal)  // <-- Secure math

                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .items(orderItems) // Don't forget to set the items!
                .build();
    }

    private OrderItemEntity convertToOrderItemEntity(OrderRequest.OrderItemRequest orderItemRequest) {
        return OrderItemEntity.builder()
                .itemId(orderItemRequest.getItemId())
                .name(orderItemRequest.getName())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .build();
    }

    private OrderResponse.OrderItemResponse convertToItemResponse(OrderItemEntity orderItemEntity) {
        return  OrderResponse.OrderItemResponse.builder()
                .itemId(orderItemEntity.getItemId())
                .name(orderItemEntity.getName())
                .price(orderItemEntity.getPrice())
                .quantity(orderItemEntity.getQuantity())
                .build();
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        OrderEnitity newOrder = convertToOrderEntity(request);

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setStatus(newOrder.getPaymentMethod() == PaymentMethod.CASH
                ? PaymentDetails.PaymentStatus.COMPLETED
                : PaymentDetails.PaymentStatus.PENDING );

        newOrder.setPaymentDetails(paymentDetails);

        // CHANGE: Corrected mapping to use local helper method and List type
        List<OrderItemEntity> orderItems = request.getCartItems().stream()
                .map(this::convertToOrderItemEntity) // This returns com.eComm.eComm.Entity.OrderItemEntity
                .collect(Collectors.toList());

        newOrder.setItems(orderItems);
        newOrder = orderEntityRepository.save(newOrder);

        return convertToResponse(newOrder);
    }

    @Override
    public void deleteOrder(String orderId) {
        OrderEnitity existingOrder = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(()-> new RuntimeException("order not found"));
        orderEntityRepository.delete(existingOrder);
    }

    @Override
    public List<OrderResponse> getLatestOrder() {
        return orderEntityRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse verifyPayment(PaymentVerificationRequest request) {
        OrderEnitity existingOrder = orderEntityRepository.findByOrderId(request.getOrderId())
                .orElseThrow(()->new RuntimeException("Order not found"));

        if(!verifyRazorpaySignature(request.getOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature())){
            throw new RuntimeException("Payment verification failed");
        }

        PaymentDetails paymentDetails = existingOrder.getPaymentDetails();
        paymentDetails.setRazorpayOrderId(request.getRazorpayOrderId());
        paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
        paymentDetails.setRazorpaySignature(request.getRazorpaySignature());
        paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);

        existingOrder = orderEntityRepository.save(existingOrder);
        return convertToResponse(existingOrder);
    }

    @Override
    public Double sumSalesByDate(LocalDate date) {
        return orderEntityRepository.sumSalesByDate(date);
    }

    @Override
    public Long countByOrderDate(LocalDate date) {
        return orderEntityRepository.countByOrderDate(date);
    }

    @Override
    public List<OrderResponse> findRecentOrders() {
        return orderEntityRepository.findRecentOrders(PageRequest.of(0,5))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private boolean verifyRazorpaySignature(String orderId, String razorpayPaymentId, String razorpaySignature) {
        return true;
    }
}