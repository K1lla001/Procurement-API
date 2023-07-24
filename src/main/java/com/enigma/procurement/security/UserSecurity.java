package com.enigma.procurement.security;

import com.enigma.procurement.entity.UserDetailsImpl;
import com.enigma.procurement.service.AdminService;
import com.enigma.procurement.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final AdminService adminService;
    private final VendorService vendorService;

    public boolean checkAdmin(Authentication authentication, String id){
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        String email = adminService.getEmailById(id);
        return principal.getEmail().equals(email);
    }

    public boolean checkVendor(Authentication authentication, String id){
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        String email = vendorService.getEmailById(id);
        return principal.getEmail().equals(email);
    }

}
