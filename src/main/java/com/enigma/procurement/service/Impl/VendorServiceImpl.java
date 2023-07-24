package com.enigma.procurement.service.Impl;

import com.enigma.procurement.entity.Vendor;
import com.enigma.procurement.models.request.VendorRequest;
import com.enigma.procurement.models.response.DetailVendorResponse;
import com.enigma.procurement.models.response.VendorResponse;
import com.enigma.procurement.repository.VendorRepository;
import com.enigma.procurement.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Vendor create(Vendor vendor) {
        try {
            vendorRepository.save(vendor);
            return vendor;
        }catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already exist");
        }
    }

    @Override
    public Vendor findById(String id) {
        return vendorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));

    }

    @Override
    public Page<VendorResponse> findAll(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<Vendor> vendors = vendorRepository.findAll(pageable);
        List<VendorResponse> vendorList = new ArrayList<>();
        for (Vendor vendor : vendors.getContent()) {
            vendorList.add(VendorResponse.builder()
                            .name(vendor.getName())
                            .phoneNumber(vendor.getPhoneNumber())
                            .street(vendor.getAddress().getStreet())
                            .city(vendor.getAddress().getCity())
                            .province(vendor.getAddress().getProvince())
                    .build());
        }

        return new PageImpl<>(vendorList, pageable, vendors.getTotalElements());
    }

    @Override
    public String getEmailById(String id) {
        Optional<Vendor> optionalVendor = vendorRepository.findVendorById(id);
        if(optionalVendor.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found!");
        return optionalVendor.get().getUserCredential().getEmail();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public DetailVendorResponse update(VendorRequest request) {
        Vendor currentVendor = findById(request.getVendorId());

        if(currentVendor != null){

            currentVendor.setName(request.getName());
            currentVendor.setPhoneNumber(request.getPhoneNumber());
            if(currentVendor.getAddress() == null){
                currentVendor.setAddress(request.getAddress());
            }else {
                currentVendor.getAddress().setStreet(request.getAddress().getStreet());
                currentVendor.getAddress().setCity(request.getAddress().getCity());
                currentVendor.getAddress().setProvince(request.getAddress().getProvince());
            }
            vendorRepository.save(currentVendor);
            return DetailVendorResponse.builder()
                    .name(currentVendor.getName())
                    .email(currentVendor.getEmail())
                    .address(currentVendor.getAddress())
                    .build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data not found!");
    }
}
