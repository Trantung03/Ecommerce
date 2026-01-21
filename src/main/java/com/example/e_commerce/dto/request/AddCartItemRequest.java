package com.example.e_commerce.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                AddCartItemRequest {

    @NotNull
    private Long skuId;

    @Min(1)
    private int quantity;

}
