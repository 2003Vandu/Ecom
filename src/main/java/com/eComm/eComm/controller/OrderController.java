package com.eComm.eComm.Controller;


import com.eComm.eComm.Service.OrderService;
import com.eComm.eComm.io.OrderRequest;
import com.eComm.eComm.io.OrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name ="Order API")
public class OrderController {

    //Dependency Injection Of orderService via this @RequiredArgsConstructor
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody OrderRequest request) {

        return orderService.createOrder(request);

    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String orderId)
    {
        orderService.deleteOrder(orderId);
    }

    @GetMapping("/latest")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getLatestOrder(){

       return orderService.getLatestOrder();

    }



}
