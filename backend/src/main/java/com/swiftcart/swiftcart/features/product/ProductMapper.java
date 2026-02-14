package com.swiftcart.swiftcart.features.product;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductResponse toResponse(Product product);

    void update(CreateProductRequest productRequest, @MappingTarget Product existingProduct);
}
