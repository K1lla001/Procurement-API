package com.enigma.procurement.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponse {

    private String orderDetailId;
    private ProductResponse product;
    private Integer quantity;

}
