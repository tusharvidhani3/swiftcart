package com.swiftcart.swiftcart.common.storage;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    public String store(MultipartFile file, String uploadDir);
    public byte[] load(String path) throws IOException;
    public void delete(String path) throws IOException;
}
