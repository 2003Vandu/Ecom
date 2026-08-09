package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.OrderService;
import com.eComm.eComm.io.DashBoardResponse;
import com.eComm.eComm.io.OrderResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name="Dashboadr API")
public class DashbordController
{
    private final OrderService orderService;

    @GetMapping
    public DashBoardResponse getDashboardData(){
        LocalDate today = LocalDate.now();
        Double todaysale = orderService.sumSalesByDate(today);
        Long todayOrderCount = orderService.countByOrderDate(today);
       List<OrderResponse> recentOrders = orderService.findRecentOrders();
       return  new DashBoardResponse(
               todaysale != null ? todaysale : 0.0,
               todayOrderCount != null ? todayOrderCount :0,
               recentOrders
       );
    }


}
