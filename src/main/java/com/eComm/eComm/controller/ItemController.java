package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.ItemService;
import com.eComm.eComm.io.ItemRequest;
import com.eComm.eComm.io.ItemResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name ="Admin API")
public class ItemController
{
    //Dependency Injection : we need to inject ItemService to call service / Business logic
    public final ItemService itemService;
    public final ObjectMapper objectMapper;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin/items")
    public ItemResponse addItem(@RequestPart("item") String itemString,
                                @RequestPart("file") MultipartFile file)
    {
        ItemRequest itemRequest = null;
        try
        {
            itemRequest = objectMapper.readValue(itemString, ItemRequest.class);
            return itemService.add(itemRequest,file);
        }catch (JsonProcessingException e)
        {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error occure While json processiong ");
        }
    }

    @GetMapping("/items")
    public List<ItemResponse> readItems(){
       return itemService.fetchItems();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/itemId/{itemId}")
    public void removeItem(@PathVariable String itemId)
    {
        try{
            itemService.deleteItem(itemId);
        }catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Item not Found");
        }
    }

}