package com.enigma.procurement.service.Impl;

import com.enigma.procurement.entity.Admin;
import com.enigma.procurement.entity.Order;
import com.enigma.procurement.entity.OrderDetail;
import com.enigma.procurement.entity.ProductPrice;
import com.enigma.procurement.models.request.OrderRequest;
import com.enigma.procurement.models.response.*;
import com.enigma.procurement.repository.OrderRepository;
import com.enigma.procurement.service.AdminService;
import com.enigma.procurement.service.OrderService;
import com.enigma.procurement.service.ProductPriceService;
import com.enigma.procurement.utils.CSVUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AdminService adminService;
    private final ProductPriceService productPriceService;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Admin buyer = adminService.findById(request.getAdminId());
        List<OrderDetail> orderDetails = request.getOrderDetails().stream().map(orderDetailRequest -> {
            ProductPrice price = productPriceService.getById(orderDetailRequest.getProductPriceId());
            return OrderDetail.builder()
                    .productPrice(price)
                    .quantity(orderDetailRequest.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        Order order = Order.builder()
                .admin(buyer)
                .transactionDate(LocalDateTime.now())
                .orderDetails(orderDetails)
                .build();

        orderRepository.saveAndFlush(order);

        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {
            orderDetail.setOrder(order);

            ProductPrice currentProduct = orderDetail.getProductPrice();
            if(currentProduct.getStock() <= 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Stock Is Empty");

            currentProduct.setStock(currentProduct.getStock() - orderDetail.getQuantity());

            productPriceService.update(currentProduct);

            return getOrderDetailResponse(orderDetail, currentProduct);
        }).collect(Collectors.toList());

        AdminResponse buyerResponse = AdminResponse.builder()
                .id(buyer.getId())
                .name(buyer.getName())
                .address(buyer.getAddress())
                .build();

        return OrderResponse.builder()
                .orderId(order.getId())
                .admin(buyerResponse)
                .transactionDate(order.getTransactionDate())
                .orderDetails(orderDetailResponses)
                .build();
    }

    private OrderDetailResponse getOrderDetailResponse(OrderDetail orderDetail, ProductPrice currentProduct) {
        return OrderDetailResponse.builder()
                .orderDetailId(orderDetail.getId())
                .quantity(orderDetail.getQuantity())
                .product(ProductResponse.builder()
                        .id(currentProduct.getProduct().getId())
                        .codeProduct(currentProduct.getProduct().getCodeProduct())
                        .productName(currentProduct.getProduct().getName())
                        .category(currentProduct.getProduct().getCategory().getName())
                        .price(currentProduct.getPrice())
                        .vendor(VendorResponse.builder()
                                .name(currentProduct.getVendor().getName())
                                .street(currentProduct.getVendor().getAddress().getStreet())
                                .city(currentProduct.getVendor().getAddress().getCity())
                                .province(currentProduct.getVendor().getAddress().getProvince())
                                .build())
                        .build())
                .build();
    }

    @Override
    public List<OrderResponse> getAllOrder() {
        List<Order> orders = orderRepository.findAll();
        return getOrderResponses(orders);
    }

    private List<OrderResponse> getOrderResponses(List<Order> orders) {
        return orders.stream().map(order -> {
            List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {
                orderDetail.setOrder(order);
                ProductPrice currentProduct = orderDetail.getProductPrice();

                return getOrderDetailResponse(orderDetail, currentProduct);
            }).collect(Collectors.toList());

            Admin admin = order.getAdmin();
            AdminResponse adminResponse = AdminResponse.builder()
                    .id(admin.getId())
                    .name(admin.getName())
                    .phoneNumber(admin.getPhoneNumber())
                    .address(admin.getAddress())
                    .build();

            return OrderResponse.builder()
                    .orderId(order.getId())
                    .admin(adminResponse)
                    .transactionDate(order.getTransactionDate())
                    .orderDetails(orderDetailResponses)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> findOrderByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findByTransactionDateBetween(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        return orders.stream().map(order -> {
            List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream().map(orderDetail -> {
                orderDetail.setOrder(order);
                ProductPrice currentProduct = orderDetail.getProductPrice();

                return getOrderDetailResponse(orderDetail, currentProduct);
            }).collect(Collectors.toList());

            Admin admin = order.getAdmin();
            AdminResponse adminResponse = AdminResponse.builder()
                    .id(admin.getId())
                    .name(admin.getName())
                    .phoneNumber(admin.getPhoneNumber())
                    .address(admin.getAddress())
                    .build();

            return OrderResponse.builder()
                    .orderId(order.getId())
                    .admin(adminResponse)
                    .orderDetails(orderDetailResponses)
                    .build();
        }).collect(Collectors.toList());

    }

    @Override
    public void transactionReports(List<OrderResponse> orders, String filePath) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<String[]> csvData = orders.stream().flatMap(order -> order.getOrderDetails().stream().map(orderDetail -> {
            String codeProduct = orderDetail.getProduct().getCodeProduct();
            String transactionDate = order.getTransactionDate().format(dateFormatter);
            String vendorName = orderDetail.getProduct().getVendor().getName();
            String productName = orderDetail.getProduct().getProductName();
            String category = orderDetail.getProduct().getCategory();
            double productPrice = orderDetail.getProduct().getPrice();
            int quantity = orderDetail.getQuantity();
            double total = productPrice * quantity;

            return new String[]{codeProduct,transactionDate, vendorName,productName, category, String.valueOf(productPrice), String.valueOf(quantity), String.valueOf(total)};
        })).collect(Collectors.toList());
        String[] header = {"Kode Barang", "Tanggal", "Nama Vendor", "Nama Barang", "Kategori", "Harga Barang", "Qty", "Total"};
        csvData.add(0, header);

        CSVUtils.writeCSVReport(csvData, filePath);
    }
}
