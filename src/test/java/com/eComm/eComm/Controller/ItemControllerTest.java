package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.ItemService;
import com.eComm.eComm.io.ItemRequest;
import com.eComm.eComm.io.ItemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    // --- 1. ADMIN: ADD ITEM (POST) ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_ShouldCreateItem() throws Exception {
        ItemRequest request = new ItemRequest();
        request.setName("Gaming Mouse");
        // Add other fields based on your ItemRequest class

        String jsonStr = objectMapper.writeValueAsString(request);

        MockMultipartFile itemPart = new MockMultipartFile("item", "", "application/json", jsonStr.getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "mouse.jpg", "image/jpeg", "fake-image".getBytes());

        // Using Mockito to skip real service logic
        when(itemService.add(any(), any())).thenReturn(new ItemResponse());

        mockMvc.perform(multipart("/admin/items")
                        .file(itemPart)
                        .file(filePart))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    // --- 2. USER: ADD ITEM (POST) -> SHOULD FAIL ---
    @Test
    @WithMockUser(roles = "USER")
    void user_ShouldBeForbiddenToCreateItem() throws Exception {
        // 1. Create the MockMultipartFile objects first
        MockMultipartFile jsonPart = new MockMultipartFile("item", "", "application/json", "{}".getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());

        // 2. Pass those objects into the multipart request
        mockMvc.perform(multipart("/admin/items")
                        .file(jsonPart)
                        .file(filePart))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // --- 3. PUBLIC/USER: GET ITEMS ---
    @Test
    @WithMockUser(roles = "USER")
    void user_ShouldFetchItems() throws Exception {
        when(itemService.fetchItems()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk());
    }

    // --- 4. ADMIN: DELETE ITEM ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_ShouldDeleteItem() throws Exception {
        String itemId = "item-123";
        doNothing().when(itemService).deleteItem(itemId);

        mockMvc.perform(delete("/admin/itemId/" + itemId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    // --- 5. USER: DELETE ITEM -> SHOULD FAIL ---
    @Test
    @WithMockUser(roles = "USER")
    void user_ShouldBeForbiddenToDeleteItem() throws Exception {
        mockMvc.perform(delete("/admin/itemId/item-123"))
                .andExpect(status().isForbidden());
    }
}