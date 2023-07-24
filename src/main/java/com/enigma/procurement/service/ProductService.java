package com.enigma.procurement.service;

import com.enigma.procurement.models.request.ProductRequest;
import com.enigma.procurement.models.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductResponse create(ProductRequest request);
    Page<ProductResponse> findAll(Integer page, Integer size);
    ProductResponse getById(String id);
    ProductResponse update(ProductRequest product);
    void deleteById(String id);

}
