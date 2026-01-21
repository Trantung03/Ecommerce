package com.example.e_commerce.service;

import com.example.e_commerce.dto.response.SkuResponse;
import org.springframework.stereotype.Service;

@Service
public interface SkuService {

    SkuResponse getSkuDetail(Long id);
}
