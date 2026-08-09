package com.eComm.eComm.Service.Implementation;

import com.eComm.eComm.Service.RazorpayService;
import com.eComm.eComm.io.RazorpayOrderResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService
{


    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Override
    public RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException {

        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orederRequest = new JSONObject();
        orederRequest.put("amount", amount*100);
        orederRequest.put("currency",currency );
        orederRequest.put("receipt", "order_rcptid_"+System.currentTimeMillis());
        orederRequest.put("payment_capture",1);

         Order order= razorpayClient.orders.create(orederRequest);

         return  convertTOResponse(order);
    }

    private RazorpayOrderResponse convertTOResponse(Order order) {

      return RazorpayOrderResponse.builder()
                .id(order.get("id"))
                .entity(order.get("entity"))
                .amount(order.get("amount"))
                .currency(order.get("currency"))
                .status(order.get("status"))
                .created_at(order.get("created_at"))
                .receipt(order.get("receipt"))
                .build();
    }


}
