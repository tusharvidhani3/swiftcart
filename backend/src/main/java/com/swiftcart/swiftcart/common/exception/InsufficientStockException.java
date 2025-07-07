package com.swiftcart.swiftcart.common.exception;


public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String msg) {
        super(msg);
    }
}
