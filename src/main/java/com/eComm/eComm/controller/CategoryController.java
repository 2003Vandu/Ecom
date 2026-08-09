package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.CategoryService;
import com.eComm.eComm.io.CategoryRequest;
import com.eComm.eComm.io.CategoryResponse;
import com.fasterxml.jackson.databind.ObjectMapper; // Ensure this import is exactly like this
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Tag(name ="Admin API")
public class CategoryController {

    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    // 1. Updated Constructor to initialize BOTH variables
    public CategoryController(CategoryService categoryService, ObjectMapper objectMapper) {
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse addcategory(@RequestPart("category") String categoryString,
                                        @RequestPart("file") MultipartFile file) {
        try {
            // 2. Use the injected objectMapper (removed the 'new' keyword)
            CategoryRequest request = objectMapper.readValue(categoryString, CategoryRequest.class);
            return categoryService.add(request, file);
        } catch (Exception e) {
            System.out.println("ERROR TYPE: " + e.getClass().getName());
            System.out.println("ERROR MESSAGE: " + e.getMessage());
            e.printStackTrace();
            // 3. Catching generic Exception or JsonProcessingException is safer here
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error parsing JSON data", e);

        }
    }

    @GetMapping("/categories")
    public List<CategoryResponse> featchcategories() {
        return categoryService.read();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/categories/{categoryId}")
    public void remove(@PathVariable String categoryId) {
        try {
            categoryService.delete(categoryId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}