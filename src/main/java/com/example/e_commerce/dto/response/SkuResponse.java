package com.example.e_commerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SkuResponse {
    private Long skuId;
    private String size;
    private String color;
    private Long price;
    private int stockAvailable;
}
