package com.swiftcart.swiftcart.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.swiftcart.swiftcart.exception.ResourceNotFoundException;

@Service
public class StorageServiceImpl implements StorageService {

    @Override
    public String store(MultipartFile file, String uploadDir) {
        Path destination = Paths.get(uploadDir, file.getOriginalFilename());
        try {
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        }
        catch(IOException ex) {
            throw new ResourceNotFoundException("File Not Found");
        }
        return destination.toString();
    }

    @Override
    public byte[] load(String path) throws IOException {
        byte[] file=Files.readAllBytes(new File(path).toPath());
        return file;
    }

    @Override
    public void delete(String path) throws IOException {
        Files.delete(Paths.get(path));
    }

}
