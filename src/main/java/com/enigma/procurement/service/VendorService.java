package com.enigma.procurement.service;

import com.enigma.procurement.entity.Vendor;
import com.enigma.procurement.models.request.VendorRequest;
import com.enigma.procurement.models.response.DetailVendorResponse;
import com.enigma.procurement.models.response.VendorResponse;
import org.springframework.data.domain.Page;

public interface VendorService {

    Vendor create(Vendor admin);
    Vendor findById(String id);
    Page<VendorResponse> findAll(Integer page, Integer size);
    String getEmailById(String id);
    DetailVendorResponse update(VendorRequest request);

}
