package com.enigma.procurement.controller;


import com.enigma.procurement.models.common.CommonResponse;
import com.enigma.procurement.models.request.AuthRequest;
import com.enigma.procurement.models.request.SignInResponse;
import com.enigma.procurement.models.request.SignUpResponse;
import com.enigma.procurement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/register-admin")
    @PreAuthorize("hasAnyRole('SUPERIOR')")
    public ResponseEntity<CommonResponse<SignUpResponse>> registerAdmin(@RequestBody AuthRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.<SignUpResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully Create Data!")
                        .data(authService.registerAdmin(request))
                        .build()
        );
    }

    @PostMapping(path = "/login")
    public ResponseEntity<CommonResponse<SignInResponse>> login(@RequestBody AuthRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.<SignInResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully login!")
                        .data(authService.login(request))
                        .build()
        );
    }

    @PostMapping(path = "/register-vendor")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CommonResponse<SignUpResponse>> registerVendor(@RequestBody AuthRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.<SignUpResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully Create Data!")
                        .data(authService.registerVendor(request))
                        .build()
        );
    }






//    CREATE 1 SUPERIOR FOR HANDLING ADMIN REGISTRATION
    @PostMapping(path = "/register-superior")
    public ResponseEntity<CommonResponse<SignUpResponse>> superior(@RequestBody AuthRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.<SignUpResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully Create Data!")
                        .data(authService.superior(request))
                        .build()
        );
    }
}
