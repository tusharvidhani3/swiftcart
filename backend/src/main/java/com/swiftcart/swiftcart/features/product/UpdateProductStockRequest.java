package com.swiftcart.swiftcart.features.product;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateProductStockRequest {

    private Long productId;
    private int changeInQty;
}
