package com.swiftcart.swiftcart.features.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(source = "images", target = "imageUrls")
    ProductResponse toResponse(Product product);

    String toImageUrl(ProductImage image);

    void update(CreateProductRequest productRequest, @MappingTarget Product existingProduct);
}
