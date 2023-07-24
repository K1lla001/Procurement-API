package com.enigma.procurement.service.Impl;

import com.enigma.procurement.entity.Admin;
import com.enigma.procurement.models.request.AdminRequest;
import com.enigma.procurement.models.response.AdminResponse;
import com.enigma.procurement.repository.AdminRepository;
import com.enigma.procurement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public Admin create(Admin admin) {
        try {
            adminRepository.save(admin);
            return admin;
        }catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already exist");
        }
    }

    @Override
    public Admin findById(String id) {
        return adminRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

    }

    @Override
    public String getEmailById(String id) {
        Optional<Admin> optionalAdmin = adminRepository.findAdminById(id);
        if(optionalAdmin.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found!");
        return optionalAdmin.get().getUserCredential().getEmail();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public AdminResponse update(AdminRequest request) {
        Admin currentAdmin = findById(request.getAdminId());

        if(currentAdmin != null){
            currentAdmin.setName(request.getName());
            currentAdmin.setPhoneNumber(request.getPhoneNumber());
            if(currentAdmin.getAddress() == null){
                currentAdmin.setAddress(request.getAddress());
            }else {
                currentAdmin.getAddress().setStreet(request.getAddress().getStreet());
                currentAdmin.getAddress().setCity(request.getAddress().getCity());
                currentAdmin.getAddress().setProvince(request.getAddress().getProvince());
            }
            adminRepository.save(currentAdmin);
            return AdminResponse.builder()
                    .name(currentAdmin.getName())
                    .email(currentAdmin.getEmail())
                    .address(currentAdmin.getAddress())
                    .build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data not found!");
    }
}
