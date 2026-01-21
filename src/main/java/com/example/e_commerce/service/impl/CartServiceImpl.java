package com.example.e_commerce.service.impl;

import com.example.e_commerce.dto.request.AddCartItemRequest;
import com.example.e_commerce.dto.response.CartItemResponse;
import com.example.e_commerce.dto.response.CartResponse;
import com.example.e_commerce.entity.Cart;
import com.example.e_commerce.entity.CartItem;
import com.example.e_commerce.entity.Sku;
import com.example.e_commerce.exception.AppException;
import com.example.e_commerce.exception.ErrorCode;
import com.example.e_commerce.repository.CartItemRepository;
import com.example.e_commerce.repository.CartRepository;
import com.example.e_commerce.repository.SkuRepository;
import com.example.e_commerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final SkuRepository skuRepository;

    @Override
    public CartResponse getCart(String sessionId) {
        Cart cart = getOrCreateCart(sessionId);
        return mapToResponse(cart);
    }

    private CartResponse mapToResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .sessionId(cart.getSessionId())
                .items(cart.getItems().stream()
                        .map(i -> CartItemResponse.builder()
                                .cartItemId(i.getId())
                                .skuId(i.getSku().getId())
                                .quantity(i.getQuantity())
                                .build())
                        .collect(Collectors.toList())).build();
    }

    private Cart getOrCreateCart(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setSessionId(sessionId);
                    cart.setCreatedAt(LocalDateTime.now());
                    return cartRepository.save(cart);
                });
    }

    @Override
    public CartResponse addItem(String sessionId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(sessionId);

        Sku sku = skuRepository.findById(request.getSkuId()).orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getSku().getId().equals(request.getSkuId()))
                .findFirst()
                .orElse(null);
        if (existingItem != null) {
            if(sku.getStockAvailable()>request.getQuantity()) {
                existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            }
            else{
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setSku(sku);
            item.setQuantity(request.getQuantity());
            cart.getItems().add(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapToResponse(cart);

    }

    @Override
    public CartResponse updateItem(String sessionId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(sessionId);

        Sku sku = skuRepository.findById(request.getSkuId()).orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));


        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getSku().getId().equals(request.getSkuId()))
                .findFirst()
                .orElse(null);
        if (existingItem != null) {
            if(sku.getStockAvailable()>sku.getStockAvailable() - request.getQuantity()) {            existingItem.setQuantity(request.getQuantity());
                existingItem.setQuantity(request.getQuantity());
            }
            else{
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setSku(sku);
            item.setQuantity(request.getQuantity());
            cart.getItems().add(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapToResponse(cart);

    }

    @Override
    public void removeItem(String sessionId, Long cartItemId) {
        Cart cart = cartRepository.findBySessionId(sessionId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        Sku sku = skuRepository.findById(cartItemId).orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));
        CartItem existingItem = cart.getItems().stream()
                .filter(i -> i.getSku().getId().equals(cartItemId))
                .findFirst()
                .orElse(null);
        if (existingItem == null) {
            throw new AppException(ErrorCode.ITEM_NOT_FOUND_IN_CART);
        } else {
            cart.getItems().remove(existingItem);
            cartRepository.save(cart);
        }
    }

    @Override
    public void clearCart(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
