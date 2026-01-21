package com.example.e_commerce.service.impl;

import com.example.e_commerce.dto.response.ProductDetailResponse;
import com.example.e_commerce.dto.response.ProductListResponse;
import com.example.e_commerce.dto.response.SkuResponse;
import com.example.e_commerce.entity.Product;
import com.example.e_commerce.entity.Sku;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.exception.ErrorCode;
import com.example.e_commerce.repository.ProductRepository;
import com.example.e_commerce.repository.SkuRepository;
import com.example.e_commerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final SkuRepository skuRepository;

    @Override
    public List<ProductListResponse> getProducts(int page, int size, Long categoryId, Long minPrice, Long maxPrice) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findProductCatalog(categoryId, minPrice, maxPrice, pageable);
        return productPage.stream().map(p -> ProductListResponse.builder().productId(p.getId())
                .name(p.getName())
                .categoryName(p.getCategory().getName())
                .price(p.getBasePrice()).build()).
                toList();
    }

    @Override
    public ProductDetailResponse getProductDetail(Long productId) {

        Product p =  productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        List<Sku> skuList = skuRepository.findByProduct_Id(productId);

        List<SkuResponse> skuResponseList = skuList.stream().map(sku -> SkuResponse.builder().skuId(sku.getId())
                .size(sku.getSize())
                .color(sku.getColor())
                .price(sku.getPrice())
                .stockAvailable(sku.getStockAvailable())
                .build()).toList();

        return ProductDetailResponse.builder().productId(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .categoryName(p.getCategory().getName())
                .skus(skuResponseList).build();
    }

}
