package com.enigma.procurement.models.request;

import com.enigma.procurement.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class VendorRequest {

    private String vendorId;
    private String name;
    private String phoneNumber;
    private Address address;
}
