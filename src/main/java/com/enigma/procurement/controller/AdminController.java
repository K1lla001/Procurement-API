package com.enigma.procurement.controller;

import com.enigma.procurement.models.common.CommonResponse;
import com.enigma.procurement.models.request.AdminRequest;
import com.enigma.procurement.models.response.AdminResponse;
import com.enigma.procurement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') and @userSecurity.checkAdmin(authentication, #request.getAdminId())")
    public ResponseEntity<CommonResponse<AdminResponse>> updateData(@RequestBody AdminRequest request){
        AdminResponse update = adminService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.<AdminResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully update data")
                        .data(update)
                        .build()
        );

    }

}
