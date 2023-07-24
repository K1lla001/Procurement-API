package com.enigma.procurement.controller;

import com.enigma.procurement.entity.Vendor;
import com.enigma.procurement.models.common.CommonResponse;
import com.enigma.procurement.models.common.PagingResponse;
import com.enigma.procurement.models.request.VendorRequest;
import com.enigma.procurement.models.response.DetailVendorResponse;
import com.enigma.procurement.models.response.VendorResponse;
import com.enigma.procurement.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/vendor")
public class VendorController {

    private final VendorService vendorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAllVendor(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ){
        Page<VendorResponse> vendors = vendorService.findAll(page, size);

        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(vendors.getTotalPages())
                .size(size)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("List of Vendors")
                        .data(vendors.getContent())
                        .paging(pagingResponse)
                        .build()
        );
    }

    @PutMapping
    @PreAuthorize("hasRole('VENDOR') and @userSecurity.checkVendor(authentication, #request.getVendorId())")
    public ResponseEntity<CommonResponse<DetailVendorResponse>> updateData(@RequestBody VendorRequest request){
        DetailVendorResponse update = vendorService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.<DetailVendorResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully update data")
                        .data(update)
                        .build()
        );

    }

}
