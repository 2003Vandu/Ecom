package com.eComm.eComm.Service.Implementation;

import com.eComm.eComm.Entity.ItemEntity;
import com.eComm.eComm.Repository.ItemRepository;
import com.eComm.eComm.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

//4/19/2026
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService  {

    private final ItemRepository itemRepository;

    @Override
    @Transactional // ✅ Ensures atomic stock update
    public void reduceStock(String itemId, Integer quantity) {
        ItemEntity item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item not found: " + itemId
                ));

        // Check if sufficient stock available
        if (item.getStockQuantity() < quantity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Insufficient stock for item %s. Available: %d, Required: %d",
                            item.getName(), item.getStockQuantity(), quantity)
            );
        }

        // Reduce stock
        item.setStockQuantity(item.getStockQuantity() - quantity);
        item.setInStock(item.getStockQuantity() > 0);

        itemRepository.save(item);

        log.info("Stock reduced for item: {} | Remaining: {}",
                itemId, item.getStockQuantity());

        // ✅ Check if stock is low
        if (item.getStockQuantity() <= item.getLowStockThreshold()) {
            log.warn("LOW STOCK ALERT: {} - Only {} units left",
                    item.getName(), item.getStockQuantity());
        }
    }

    @Override
    @Transactional
    public void increaseStock(String itemId, Integer quantity) {
        ItemEntity item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item not found: " + itemId
                ));

        item.setStockQuantity(item.getStockQuantity() + quantity);
        item.setInStock(true);

        itemRepository.save(item);

        log.info("Stock increased for item: {} | New quantity: {}",
                itemId, item.getStockQuantity());
    }

    @Override
    @Transactional
    public void updateStock(String itemId, Integer newQuantity) {
        ItemEntity item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item not found: " + itemId
                ));

        item.setStockQuantity(newQuantity);
        item.setInStock(newQuantity > 0);

        itemRepository.save(item);
    }

    @Override
    public boolean checkStockAvailability(String itemId, Integer requiredQuantity) {
        ItemEntity item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item not found: " + itemId
                ));

        return item.getStockQuantity() >= requiredQuantity;
    }

    @Override
    public List<String> getLowStockItems() {
        return itemRepository.findAll().stream()
                .filter(item -> item.getStockQuantity() <= item.getLowStockThreshold())
                .map(ItemEntity::getItemId)
                .collect(Collectors.toList());
    }
}