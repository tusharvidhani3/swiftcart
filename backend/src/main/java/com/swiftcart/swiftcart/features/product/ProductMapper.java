package com.swiftcart.swiftcart.features.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(source = "images", target = "imageUrls")
    ProductResponse toResponse(Product product);

    default String toImageUrl(ProductImage image) {
        return image != null ? image.getImageUrl() : null;
    }

    void update(ProductRequest productRequest, @MappingTarget Product existingProduct);
}
