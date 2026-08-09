package com.eComm.eComm.Service;

import java.util.List;

//4/19/2026
public interface InventoryService
{
    void reduceStock(String itemId, Integer quantity);
    void increaseStock(String itemId, Integer quantity);
    void updateStock(String itemId, Integer newQuantity);
    boolean checkStockAvailability(String itemId, Integer requiredQuantity);
    List<String> getLowStockItems();
}
