package com.uade.tg.services;

import com.uade.tg.exceptions.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads/reviews}")
    private String uploadDir;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Directorio de almacenamiento creado: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException("No se pudo crear el directorio de almacenamiento de archivos.");
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            if (file.isEmpty()) {
                throw new BusinessException("El archivo está vacío: " + originalFilename);
            }

            if (originalFilename.contains("..")) {
                throw new BusinessException("El nombre del archivo contiene una secuencia de ruta inválida: " + originalFilename);
            }

            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Archivo guardado exitosamente: {}", uniqueFilename);
            return uniqueFilename;
        } catch (IOException ex) {
            log.error("Error al guardar archivo: {}", originalFilename, ex);
            throw new BusinessException("No se pudo almacenar el archivo " + originalFilename + ". Por favor intente de nuevo.");
        }
    }

    public Path loadFile(String filename) {
        return this.fileStorageLocation.resolve(filename).normalize();
    }

    public boolean deleteFile(String filename) {
        try {
            Path filePath = loadFile(filename);
            Files.deleteIfExists(filePath);
            log.info("Archivo eliminado: {}", filename);
            return true;
        } catch (IOException ex) {
            log.error("Error al eliminar archivo: {}", filename, ex);
            return false;
        }
    }
}

