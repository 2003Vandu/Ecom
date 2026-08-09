package com.eComm.eComm.serviceTest;

import com.eComm.eComm.Repository.CategoryRepository;
import com.eComm.eComm.Repository.ItemRepository; // Add this
import com.eComm.eComm.Service.Implementation.FileUploadServiceImpl;
import com.eComm.eComm.Service.Implementation.categoryServiceimpl;
import com.eComm.eComm.io.CategoryRequest;
import com.eComm.eComm.io.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


// 1. MUST add these static imports for when, any, assert
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ItemRepository itemRepository; // 2. MUST mock this (used in convertToResponse)

    @Mock
    private FileUploadServiceImpl fileUploadService;

    @InjectMocks
    private categoryServiceimpl categoryService;

    @Test
    void addCategory_ShouldWork() {
        // Setup data
        CategoryRequest request = new CategoryRequest("Test", "Desc", "#fff");

        // 3. Define Mock behavior
        when(fileUploadService.uploadFile(any())).thenReturn("http://aws-url.com");

        // Mock save to return the entity passed to it
        when(categoryRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // Mock item count (since it's called in convertToResponse)
        when(itemRepository.countByCategoryId(any())).thenReturn(5);

        // Execute
        CategoryResponse response = categoryService.add(request, null);

        // 4. Assertions
        assertNotNull(response);
        assertNotNull(response.getCategoryId());
        assertEquals("Test", response.getName());
        assertEquals(5, response.getItems());
    }
}