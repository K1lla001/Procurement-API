package com.enigma.procurement.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private String id;
    private String codeProduct;
    private String productName;
    private String description;
    private String category;
    private Long price;
    private Integer stock;
    private VendorResponse vendor;

}
