package com.example.e_commerce.exception;

public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(500, "Uncategorized exception"),
    INVALID_REQUEST(400, "Invalid request"),
    PRODUCT_NOT_FOUND(404, "Product not found"),
    SKU_NOT_FOUND(404, "SKU not found"),
    CART_NOT_FOUND(404, "Cart not found"),
    OUT_OF_STOCK(409, "Out of stock"),
    ORDER_EXPIRED(409, "Order expired"),
    CART_EMPTY(400, "Cart is empty"),
    ITEM_NOT_FOUND_IN_CART(401, "Item not found in the cart"),
    INVALID_CHECKOUT_TOKEN(403, "Invalid checkout token"),;


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
