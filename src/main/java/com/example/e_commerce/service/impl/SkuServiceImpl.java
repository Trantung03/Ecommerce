package com.example.e_commerce.service.impl;

import com.example.e_commerce.dto.response.ProductDetailResponse;
import com.example.e_commerce.dto.response.SkuResponse;
import com.example.e_commerce.entity.Sku;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.exception.ErrorCode;
import com.example.e_commerce.repository.SkuRepository;
import com.example.e_commerce.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkuServiceImpl implements SkuService {

    private final SkuRepository skuRepository;

    @Override
    public SkuResponse getSkuDetail(Long id) {

        Sku sku = skuRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.SKU_NOT_FOUND));

        return SkuResponse.builder().skuId(sku.getId())
                .size(sku.getSize())
                .color(sku.getColor())
                .price(sku.getPrice())
                .stockAvailable(sku.getStockAvailable()).build();
    }
}
