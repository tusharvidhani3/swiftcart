package com.swiftcart.swiftcart.exception;


public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String msg) {
        super(msg);
    }
}
