package com.enigma.procurement.controller;

import com.enigma.procurement.models.common.CommonResponse;
import com.enigma.procurement.models.request.OrderRequest;
import com.enigma.procurement.models.response.OrderResponse;
import com.enigma.procurement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') and @userSecurity.checkAdmin(authentication, #request.getAdminId())")
    public ResponseEntity<?> createNewTransaction(@RequestBody OrderRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully create new transaction")
                        .data(orderResponse)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTransaction() {
        List<OrderResponse> allOrder = orderService.getAllOrder();
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("List of Transaction")
                        .data(allOrder)
                        .build()
        );
    }

    @GetMapping(path = "by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrdersByDateRange(
            @RequestParam("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        List<OrderResponse> orderByDateRange = orderService.findOrderByDateRange(startDate, endDate);

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Transaction By Date")
                        .data(orderByDateRange)
                        .build()
        );
    }

    @GetMapping(path = "/download")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> generateTransactionReport() {
        String userHome = System.getProperty("user.home");
        String downloadFolderPath = userHome + File.separator + "Downloads";
        String fileName = "file.csv";
        String filePath = downloadFolderPath + File.separator + fileName;

        List<OrderResponse> allOrder = orderService.getAllOrder();
        orderService.transactionReports(allOrder, filePath);

        return ResponseEntity.ok().build();
    }


}
