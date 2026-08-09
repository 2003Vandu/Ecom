package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.InventoryService;
import com.eComm.eComm.io.StockUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//4/19/2026
@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // Update stock manually
    @PutMapping("/{itemId}/stock")
    @ResponseStatus(HttpStatus.OK)
    public void updateStock(
            @PathVariable String itemId,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        inventoryService.updateStock(itemId, request.getQuantity());
    }

    // Increase stock (e.g., new inventory received)
    @PutMapping("/{itemId}/increase")
    @ResponseStatus(HttpStatus.OK)
    public void increaseStock(
            @PathVariable String itemId,
            @Valid @RequestBody StockUpdateRequest request
    ) {
        inventoryService.increaseStock(itemId, request.getQuantity());
    }

    // Get low stock items
    @GetMapping("/low-stock")
    public List<String> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }
}