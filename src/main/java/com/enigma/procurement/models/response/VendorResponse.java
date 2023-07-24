package com.enigma.procurement.models.response;

import com.enigma.procurement.entity.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorResponse {

    private String name;
    private String phoneNumber;
    private String street;
    private String city;
    private String province;

}
