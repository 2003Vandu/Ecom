package com.eComm.eComm.Service.Implementation;

import com.eComm.eComm.Repository.CategoryRepository;
import com.eComm.eComm.Repository.ItemRepository;
import com.eComm.eComm.Service.CategoryService;
import com.eComm.eComm.Entity.CategoryEntity;
import com.eComm.eComm.io.CategoryRequest;
import com.eComm.eComm.io.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class categoryServiceimpl implements CategoryService
{

    private final CategoryRepository categoryRepository;

    private final ItemRepository itemRepository;

    private final com.eComm.eComm.Service.Implementation.FileUploadServiceImpl fileUploadService;

    // create category
    @Override
    public CategoryResponse add(CategoryRequest request , MultipartFile file) {

        // to save in s3 Bucket
        String imgUrl = fileUploadService.uploadFile(file);
        CategoryEntity newCategory = convertToEntity(request);
        // to save in s3 Bucket
        newCategory.setImgUrl(imgUrl);
        newCategory = categoryRepository.save(newCategory);
        return convertToResponse(newCategory);

    }
    // get all Categoryes
    @Override
    public List<CategoryResponse> read() {

        return categoryRepository.findAll()
                .stream()
                .map(CategoryEntity -> convertToResponse(CategoryEntity))
                .collect(Collectors.toList());
    }

    //    Delete catagory By id
    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(()->new RuntimeException("Category ID not Found"));

        //it will the file from aws s3 bucket
        fileUploadService.deleteFile(existingCategory.getImgUrl());
        // also delete a category
        categoryRepository.delete(existingCategory);
    }

    private CategoryResponse convertToResponse(CategoryEntity newCategory) {
        //Add to get itemcount
        Integer itemsCount =  itemRepository.countByCategoryId(newCategory.getId());
        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .imgUrl(newCategory.getImgUrl())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .items(itemsCount)
                .build();

    }

    private CategoryEntity convertToEntity(CategoryRequest request)
    {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();


    }
}
