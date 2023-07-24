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
public class AdminRequest {

    private String adminId;
    private String name;
    private String phoneNumber;
    private Address address;
}
