package com.enigma.procurement.service.Impl;

import com.enigma.procurement.entity.*;
import com.enigma.procurement.entity.constraint.ERole;
import com.enigma.procurement.models.request.AuthRequest;
import com.enigma.procurement.models.request.SignInResponse;
import com.enigma.procurement.models.request.SignUpResponse;
import com.enigma.procurement.repository.UserCredentialRepository;
import com.enigma.procurement.security.BcryptUtils;
import com.enigma.procurement.security.JwtUtil;
import com.enigma.procurement.service.AdminService;
import com.enigma.procurement.service.AuthService;
import com.enigma.procurement.service.RoleService;
import com.enigma.procurement.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final RoleService roleService;
    private final BcryptUtils bcryptUtil;
    private final AdminService adminService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final VendorService vendorService;
    @Override
    @Transactional(rollbackOn = Exception.class)
    public SignUpResponse registerAdmin(AuthRequest request) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_ADMIN);
            UserCredential user = createUserCredential(request, role);

            Admin admin = Admin.builder()
                    .name(nameGenerator(user.getEmail()))
                    .email(user.getEmail())
                    .userCredential(user)
                    .build();

            adminService.create(admin);

            return SignUpResponse.builder().email(user.getEmail()).build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user already exist");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public SignUpResponse registerVendor(AuthRequest request) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_VENDOR);
            UserCredential user = createUserCredential(request, role);

            Vendor vendor = Vendor.builder()
                    .name(nameGenerator(user.getEmail()))
                    .email(user.getEmail())
                    .userCredential(user)
                    .build();

            vendorService.create(vendor);

            return SignUpResponse.builder().email(user.getEmail()).build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user already exist");
        }
    }

    private UserCredential createUserCredential(AuthRequest request, Role role) {
        UserCredential user = UserCredential.builder()
                .email(request.getEmail())
                .password(bcryptUtil.hashPassword(request.getPassword()))
                .role(role)
                .build();
        userCredentialRepository.saveAndFlush(user);
        return user;
    }


    @Override
    public SignUpResponse superior(AuthRequest request) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_SUPERIOR);
            UserCredential user = createUserCredential(request, role);

            return SignUpResponse.builder().email(user.getEmail()).build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user already exist");
        }
    }

    @Override
    public SignInResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String myRole = "";
        if (!roles.isEmpty()) myRole = roles.get(0);

        String jwtToken = jwtUtil.generateToken(userDetails.getEmail());

        return SignInResponse.builder()
                .email(userDetails.getEmail())
                .role(myRole)
                .token(jwtToken)
                .build();
    }


    private String nameGenerator(String name) {
        int postIdx = name.indexOf("@");
        if (postIdx != -1) {
            return name.substring(0, postIdx);
        }
        return name;
    }
}
