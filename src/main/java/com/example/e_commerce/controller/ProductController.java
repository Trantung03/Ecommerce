package com.example.e_commerce.controller;

import com.example.e_commerce.dto.response.ApiResponse;
import com.example.e_commerce.dto.response.ProductDetailResponse;
import com.example.e_commerce.dto.response.ProductListResponse;
import com.example.e_commerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductListResponse>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<ProductListResponse> result = productService.getProducts(page, size, categoryId, minPrice, maxPrice);

        return ApiResponse.<List<ProductListResponse>>builder().result(result).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.<ProductDetailResponse>builder().result(productService.getProductDetail(id)).build();
    }
}
