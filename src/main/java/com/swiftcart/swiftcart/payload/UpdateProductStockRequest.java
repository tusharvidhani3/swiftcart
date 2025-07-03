package com.swiftcart.swiftcart.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateProductStockRequest {

    private Long sellerProductId;
    private int changeInQty;
}
