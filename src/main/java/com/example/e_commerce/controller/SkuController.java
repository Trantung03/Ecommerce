package com.example.e_commerce.controller;


import com.example.e_commerce.dto.response.ApiResponse;
import com.example.e_commerce.dto.response.SkuResponse;
import com.example.e_commerce.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sku")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @GetMapping("/{id}")
    public ApiResponse<SkuResponse> detail(@PathVariable Long id) {
        return ApiResponse.<SkuResponse>builder().result(skuService.getSkuDetail(id)).build();
    }
}
