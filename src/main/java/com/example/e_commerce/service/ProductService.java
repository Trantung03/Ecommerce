package com.example.e_commerce.service;

import com.example.e_commerce.dto.response.ProductDetailResponse;
import com.example.e_commerce.dto.response.ProductListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<ProductListResponse> getProducts(int page, int size, Long categoryId, Long minPrice, Long maxPrice);

    ProductDetailResponse getProductDetail(Long productId);


}
