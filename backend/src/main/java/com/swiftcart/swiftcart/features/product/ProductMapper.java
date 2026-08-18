package com.swiftcart.swiftcart.features.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(source = "images", target = "imageUrls")
    @Mapping(target = "isOutOfStock", expression = "java(product.getStock() == 0)")
    ProductResponse toResponse(Product product);

    default String toImageUrl(ProductImage image) {
        return image != null ? image.getImageUrl() : null;
    }

    @Mapping(target = "images", ignore = true)
    void update(ProductRequest productRequest, @MappingTarget Product existingProduct);

    @Mapping(source = "images", target = "imageUrls")
    SellerProductResponse toSellerResponse(Product product);
}
