package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.OrderService;
import com.eComm.eComm.io.OrderRequest;
import com.eComm.eComm.io.OrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Covers: JSON Request Body processing, Path Variables, and Dashboard data retrieval.
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    // --- 1. TEST: CREATE ORDER (POST /orders) ---
    @Test
    @WithMockUser(roles = "USER") // Users should be able to create orders
    void user_ShouldCreateOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        // Set fields for your OrderRequest here...

        String jsonRequest = objectMapper.writeValueAsString(request);

        when(orderService.createOrder(any())).thenReturn(new OrderResponse());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    // --- 2. TEST: GET LATEST ORDERS (GET /orders/latest) ---
    @Test
    @WithMockUser(roles = "USER")
    void user_ShouldFetchLatestOrders() throws Exception {
        when(orderService.getLatestOrder()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/latest"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    // --- 3. TEST: DELETE ORDER (DELETE /orders/{id}) ---
    @Test
    @WithMockUser(roles = "ADMIN") // Usually only Admins delete orders
    void admin_ShouldDeleteOrder() throws Exception {
        String orderId = "ORD-123";
        doNothing().when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/orders/" + orderId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    // --- 4. TEST: SECURITY (GUEST ACCESS) ---
    @Test
    void guest_ShouldNotAccessOrders() throws Exception {
        // No @WithMockUser means we are testing an unauthenticated guest
        mockMvc.perform(get("/orders/latest"))
                .andExpect(status().isForbidden());
    }
}