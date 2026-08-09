package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.CategoryService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// ----------------------------------------------

// Covers: Admin access, Multipart file uploads to S3 (Mocked), and Role-Based Access Control.
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    // --- 1. TEST: ADMIN ADD CATEGORY (POST) ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_ShouldCreateCategory() throws Exception {
        // Setup data
        com.eComm.eComm.io.CategoryRequest request = new com.eComm.eComm.io.CategoryRequest();
        request.setName("Electronics");
        String jsonStr = objectMapper.writeValueAsString(request);

        MockMultipartFile categoryPart = new MockMultipartFile("category", "", "application/json", jsonStr.getBytes());
        MockMultipartFile filePart = new MockMultipartFile("file", "test.jpg", "image/jpeg", "image-data".getBytes());

        when(categoryService.add(any(), any())).thenReturn(new com.eComm.eComm.io.CategoryResponse());

        mockMvc.perform(multipart("/admin/categories")
                        .file(categoryPart)
                        .file(filePart))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    // --- 2. TEST: USER ADD CATEGORY (POST) -> SHOULD FAIL ---
    @Test
    @WithMockUser(roles = "USER")
    void user_ShouldBeForbiddenToCreateCategory() throws Exception {
        MockMultipartFile filePart = new MockMultipartFile("file", "test.jpg", "text/plain", "data".getBytes());

        mockMvc.perform(multipart("/admin/categories")
                        .file("category", "{}".getBytes())
                        .file(filePart))
                .andDo(print())
                .andExpect(status().isForbidden()); // Security blocks this
    }

    // --- 3. TEST: GET CATEGORIES (AVAILABLE TO ALL LOGGED IN) ---
    @Test
    @WithMockUser(roles = "USER")
    void user_ShouldFetchCategories() throws Exception {
        when(categoryService.read()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk());
    }

    // --- 4. TEST: ADMIN DELETE CATEGORY ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_ShouldDeleteCategory() throws Exception {
        String categoryId = "123-abc";
        doNothing().when(categoryService).delete(categoryId);

        mockMvc.perform(delete("/admin/categories/" + categoryId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    // --- 5. TEST: USER DELETE CATEGORY -> SHOULD FAIL ---
    @Test
    @WithMockUser(roles = "USER")
    void user_ShouldBeForbiddenToDeleteCategory() throws Exception {
        mockMvc.perform(delete("/admin/categories/123-abc"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
