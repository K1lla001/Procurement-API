package com.enigma.procurement.models.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponse {

    private Integer currentPage;
    private Integer totalPage;
    private Integer size;

}
