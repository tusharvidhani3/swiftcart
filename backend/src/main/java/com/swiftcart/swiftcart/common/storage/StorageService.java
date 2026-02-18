package com.swiftcart.swiftcart.common.storage;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(MultipartFile file, String uploadDir);
    byte[] load(String path) throws IOException;
    void delete(String path) throws IOException;
}
