package com.example.e_commerce.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListResponse {

    private Long productId;
    private String name;
    private String categoryName;
    private Long price;

}
