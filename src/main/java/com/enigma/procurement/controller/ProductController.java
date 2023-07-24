package com.enigma.procurement.controller;

import com.enigma.procurement.models.common.CommonResponse;
import com.enigma.procurement.models.common.PagingResponse;
import com.enigma.procurement.models.request.ProductRequest;
import com.enigma.procurement.models.response.ProductResponse;
import com.enigma.procurement.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/products")
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<?> getAllProduct(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ){
        Page<ProductResponse> pageableProduct = productService.findAll(page, size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(pageableProduct.getTotalPages())
                .size(size)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("List of products")
                        .data(pageableProduct.getContent())
                        .paging(pagingResponse)
                        .build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR') and @userSecurity.checkVendor(authentication, #request.getVendorId())")
    public ResponseEntity<CommonResponse<ProductResponse>> createProduct (@RequestBody ProductRequest request){
        ProductResponse productResponse = productService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
          CommonResponse.<ProductResponse>builder()
                  .statusCode(HttpStatus.CREATED.value())
                  .message("Successfully Created Data!")
                  .data(productResponse)
                  .build()
        );
    }

    @PutMapping
    @PreAuthorize("hasRole('VENDOR') and  @userSecurity.checkVendor(authentication, #request.getVendorId())")
    public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(@RequestBody ProductRequest request){
        ProductResponse update = productService.update(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.<ProductResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully Update Product")
                        .data(update)
                        .build()
        );
    }
}
