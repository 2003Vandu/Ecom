package com.eComm.eComm.Controller;

import com.eComm.eComm.Service.OrderService;
import com.eComm.eComm.Service.RazorpayService;
import com.eComm.eComm.io.OrderResponse;
import com.eComm.eComm.io.PaymentRequest;
import com.eComm.eComm.io.PaymentVerificationRequest;
import com.eComm.eComm.io.RazorpayOrderResponse;
import com.razorpay.RazorpayException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name ="Payment API")
public class PaymentController
{

      private final RazorpayService razorpayService;
      private final OrderService orderService;

      @PostMapping("/create-order")
      @ResponseStatus(HttpStatus.CREATED)
      public RazorpayOrderResponse createRazorpayOrder(@RequestBody PaymentRequest request)  throws RazorpayException
      {
        return razorpayService.createOrder(request.getAmount(),request.getCurrency());
      }

      @PostMapping("/verify")
       public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request)
       { // inside this we are going to use of the order service

           return orderService.verifyPayment(request);

       }
}
