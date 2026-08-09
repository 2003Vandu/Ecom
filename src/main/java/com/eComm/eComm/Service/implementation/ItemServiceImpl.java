package com.eComm.eComm.Service.Implementation;

import com.eComm.eComm.Entity.CategoryEntity;
import com.eComm.eComm.Entity.ItemEntity;
import com.eComm.eComm.Repository.CategoryRepository;
import com.eComm.eComm.Repository.ItemRepository;
import com.eComm.eComm.Service.ItemService;
import com.eComm.eComm.io.ItemRequest;
import com.eComm.eComm.io.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//@Service
//@RequiredArgsConstructor
//public class ItemServiceImpl implements ItemService
//{
//
//    // Dependency injection
//    private final FileUploadServiceImpl fileUploadService;
//    private final CategoryRepository categoryRepository;
//    private final ItemRepository itemRepository;
//
//
//
//    @Override
//    public ItemResponse add(ItemRequest request, MultipartFile file)
//    {
//        String imagUrl = fileUploadService.uploadFile(file);
//        ItemEntity newItem = convertToEntity(request);
//        CategoryEntity existingCategory= categoryRepository.findByCategoryId(request.getCategoryId())
//                .orElseThrow(()-> new RuntimeException("Category not found"+request.getCategoryId()));
//
//        newItem.setCategory(existingCategory);
//        newItem.setImgUrl(imagUrl);
//
//        newItem = itemRepository.save(newItem) ;
//
//        return   convertToResponse(newItem);
//    }
//
//    private ItemResponse convertToResponse(ItemEntity newItem)
//    {
//         return   ItemResponse.builder()
//                   .itemId(newItem.getItemId())
//                   .name(newItem.getName())
//                   .description(newItem.getDescription())
//                   .price(newItem.getPrice())
//                   .imgUrl(newItem.getImgUrl())
//                   .categoryName(newItem.getCategory().getName())
//                   .categoryId(newItem.getCategory().getCategoryId())
//                   .createdAt(newItem.getCreatedAt())
//                   .updatedAt(newItem.getUpdatedAt())
//                   .build();
//
//    }
//
//    private ItemEntity convertToEntity(ItemRequest request)
//    {
//        return ItemEntity.builder()
//                .itemId(UUID.randomUUID().toString())
//                .name(request.getName())
//                .description(request.getDescription())
//                .price(request.getPrice())
//                .build();
//
//    }
//
//    @Override
//    public List<ItemResponse> fetchItems() {
//       return itemRepository.findAll()
//                .stream()
//                .map(ItemEntity -> convertToResponse(ItemEntity))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public void deleteItem(String itemId) {
//
//        ItemEntity existingItem =  itemRepository.findByItemId(itemId)
//                .orElseThrow(()-> new RuntimeException("Item not found"));
//
//        boolean isFileDelete = fileUploadService.deleteFile(existingItem.getImgUrl());
//        if(isFileDelete)
//        {
//            itemRepository.delete(existingItem);
//        }
//        else {
//            throw  new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unnable to Delete Image ");
//        }
//
//
//    }
//}

//4/19/2026
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final FileUploadServiceImpl fileUploadService;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional // ✅ Add transaction
    public ItemResponse add(ItemRequest request, MultipartFile file) {
        String imagUrl = fileUploadService.uploadFile(file);
        ItemEntity newItem = convertToEntity(request);

        CategoryEntity existingCategory = categoryRepository
                .findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Category not found: " + request.getCategoryId()
                ));

        newItem.setCategory(existingCategory);
        newItem.setImgUrl(imagUrl);

        // ✅ Set stock fields
        newItem.setStockQuantity(request.getStockQuantity());
        newItem.setLowStockThreshold(request.getLowStockThreshold());
        newItem.setInStock(request.getStockQuantity() > 0);

        newItem = itemRepository.save(newItem);

        return convertToResponse(newItem);
    }

    private ItemResponse convertToResponse(ItemEntity item) {
        return ItemResponse.builder()
                .itemId(item.getItemId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imgUrl(item.getImgUrl())
                .categoryName(item.getCategory().getName())
                .categoryId(item.getCategory().getCategoryId())
                // ✅ Include stock information
                .stockQuantity(item.getStockQuantity())
                .lowStockThreshold(item.getLowStockThreshold())
                .inStock(item.getInStock())
                .isLowStock(item.getStockQuantity() <= item.getLowStockThreshold())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private ItemEntity convertToEntity(ItemRequest request) {
        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .lowStockThreshold(request.getLowStockThreshold())
                .build();
    }

    @Override
    public List<ItemResponse> fetchItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItem(String itemId) {
        ItemEntity existingItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        boolean isFileDelete = fileUploadService.deleteFile(existingItem.getImgUrl());

        if (isFileDelete) {
            itemRepository.delete(existingItem);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to delete image"
            );
        }
    }
}
