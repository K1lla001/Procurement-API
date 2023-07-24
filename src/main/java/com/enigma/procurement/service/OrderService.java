package com.enigma.procurement.service;

import com.enigma.procurement.models.request.OrderRequest;
import com.enigma.procurement.models.response.OrderResponse;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

     OrderResponse createOrder(OrderRequest request);

     List<OrderResponse> getAllOrder();

     List<OrderResponse> findOrderByDateRange(LocalDate startDate, LocalDate endDate);

     void transactionReports(List<OrderResponse> orders, String filePath);
}
