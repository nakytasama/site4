package org.example.site4.service;

import org.example.site4.domain.Image;
import org.example.site4.security.domain.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface ImageService {
    List<Image> getAllImages();
    List<Image> getUserImages(Long userId);
    List<Image> getImagesByCategory(Long categoryId);
    List<Image> searchImages(String query);
    Optional<Image> getImageById(Long id);
    Image saveImage(Image image, MultipartFile file, User user, Long categoryId);
    Image updateImage(Long id, Image imageDetails, MultipartFile file, Long categoryId);
    void deleteImage(Long id);
    List<Image> getRecentImages(int count);
}