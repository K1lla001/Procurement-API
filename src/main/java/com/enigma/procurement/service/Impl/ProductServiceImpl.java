package com.enigma.procurement.service.Impl;

import com.enigma.procurement.entity.Category;
import com.enigma.procurement.entity.Product;
import com.enigma.procurement.entity.ProductPrice;
import com.enigma.procurement.entity.Vendor;
import com.enigma.procurement.models.request.ProductRequest;
import com.enigma.procurement.models.response.ProductResponse;
import com.enigma.procurement.models.response.VendorResponse;
import com.enigma.procurement.repository.ProductRepository;
import com.enigma.procurement.service.ProductPriceService;
import com.enigma.procurement.service.ProductService;
import com.enigma.procurement.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductPriceService productPriceService;
    private final VendorService vendorService;

    @Override
    public ProductResponse create(ProductRequest request) {
        Vendor vendor = vendorService.findById(request.getVendorId());

        Product product = Product.builder()
                .name(request.getProductName())
                .codeProduct(codeGenerator())
                .description(request.getDescription())
                .category(Category.builder().name(request.getCategory()).build())
                .build();
        productRepository.saveAndFlush(product);

        ProductPrice productPrice = ProductPrice.builder()
                .price(request.getPrice())
                .stock(request.getStock())
                .vendor(vendor)
                .product(product)
                .isActive(true)
                .build();
        productPriceService.create(productPrice);

        return toProductResponse(product, productPrice, vendor);

    }

    @Override
    public Page<ProductResponse> findAll(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products.getContent()) {
            Optional<ProductPrice> productPrice = product.getProductPrices()
                    .stream()
                    .filter(ProductPrice::getIsActive).findFirst();

            if (productPrice.isEmpty()) continue;
            Vendor vendor = productPrice.get().getVendor();

            productResponses.add(toProductResponse(product, productPrice.get(), vendor));
        }

        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

    @Override
    public ProductResponse getById(String id) {
        return null;
    }

    @Override
    public ProductResponse update(ProductRequest request) {
        Product currentProduct = findByIdOrThrowNotFound(request.getProductId());
        currentProduct.setName(request.getProductName());
        currentProduct.setDescription(request.getDescription());

        ProductPrice productPriceActive = productPriceService.findProductPriceActive(request.getProductId(), true);

        if (!productPriceActive.getVendor().getId().equals(request.getVendorId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to modify this product!");
        }
        if (!request.getPrice().equals(productPriceActive.getPrice())) {
            productPriceActive.setIsActive(false);
            ProductPrice productPrice = productPriceService.create(ProductPrice.builder()
                    .price(request.getPrice())
                    .stock(request.getStock())
                    .product(currentProduct)
                    .vendor(productPriceActive.getVendor())
                    .isActive(true)
                    .build());
            currentProduct.addProductPrice(productPrice);
            return toProductResponse(currentProduct, productPrice, productPrice.getVendor());
        }
        productPriceActive.setStock(request.getStock());

        return toProductResponse(currentProduct, productPriceActive, productPriceActive.getVendor());
    }

    @Override
    public void deleteById(String id) {

    }

    private ProductResponse toProductResponse(Product product, ProductPrice productPrice, Vendor vendor) {
        return ProductResponse.builder()
                .id(product.getId())
                .codeProduct(product.getCodeProduct())
                .productName(product.getName())
                .category(product.getCategory().getName())
                .description(product.getDescription())
                .price(productPrice.getPrice())
                .stock(productPrice.getStock())
                .vendor(VendorResponse.builder().name(vendor.getName())
                        .street(vendor.getAddress().getStreet())
                        .city(vendor.getAddress().getCity())
                        .province(vendor.getAddress().getProvince())
                        .build())
                .build();
    }


    private Product findByIdOrThrowNotFound(String id) {
        return productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
    }
    private String codeGenerator() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new SecureRandom();
        Integer codeLength = 10;
        StringBuilder codeBuilder = new StringBuilder(codeLength);

        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            codeBuilder.append(randomChar);
        }

        return codeBuilder.toString();
    }
}
