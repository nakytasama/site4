package org.example.site4.service;

import lombok.RequiredArgsConstructor;
import org.example.site4.domain.Category;
import org.example.site4.domain.Image;
import org.example.site4.security.domain.User;
import org.example.site4.repository.CategoryRepository;
import org.example.site4.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;

    @Value("${upload.path:uploads}")
    private String uploadPath;

    @Override
    public List<Image> getAllImages() {
        return imageRepository.findAllWithUsers();
    }

    @Override
    public List<Image> getUserImages(Long userId) {
        return imageRepository.findByUserId(userId);
    }

    @Override
    public List<Image> getImagesByCategory(Long categoryId) {
        return imageRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Image> searchImages(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllImages();
        }
        return imageRepository.searchByTitleOrDescription(query.trim());
    }

    @Override
    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    @Override
    public Image saveImage(Image image, MultipartFile file, User user, Long categoryId) {
        validateFile(file);

        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = generateFileName(file);

            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            image.setImagePath(fileName);
            image.setUser(user);

            if (categoryId != null) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Категория не найдена"));
                image.setCategory(category);
            }

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    @Override
    public Image updateImage(Long id, Image imageDetails, MultipartFile file, Long categoryId) {
        return imageRepository.findById(id)
                .map(image -> {
                    if (imageDetails.getTitle() != null) {
                        image.setTitle(imageDetails.getTitle());
                    }
                    if (imageDetails.getDescription() != null) {
                        image.setDescription(imageDetails.getDescription());
                    }

                    if (categoryId != null) {
                        Category category = categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
                        image.setCategory(category);
                    } else if (categoryId == null) {
                        image.setCategory(null);
                    }

                    // Обновляем файл если передан новый
                    if (file != null && !file.isEmpty()) {
                        validateFile(file);
                        updateImageFile(image, file);
                    }

                    return imageRepository.save(image);
                })
                .orElseThrow(() -> new RuntimeException("Изображение не найдено"));
    }

    @Override
    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Изображение не найдено"));

        try {
            // Удаляем файл
            Path filePath = Paths.get(uploadPath, image.getImagePath());
            Files.deleteIfExists(filePath);

            imageRepository.delete(image);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл изображения", e);
        }
    }

    @Override
    public List<Image> getRecentImages(int count) {
        PageRequest pageRequest = PageRequest.of(0, count);
        return imageRepository.findByOrderByCreatedAtDesc(pageRequest);
    }

    // Вспомогательные методы

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Файл не может быть пустым");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Можно загружать только изображения");
        }

        // Проверяем размер файла (максимум 100MB)
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new RuntimeException("Размер файла не должен превышать 100MB");
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Убираем небезопасные символы из имени файла
        String safeName = originalFileName != null ?
                originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_") : "image";

        return UUID.randomUUID() + "_" + safeName;
    }

    private void updateImageFile(Image image, MultipartFile newFile) {
        try {
            // Удаляем старый файл
            Path oldFilePath = Paths.get(uploadPath, image.getImagePath());
            Files.deleteIfExists(oldFilePath);

            // Сохраняем новый файл
            String fileName = generateFileName(newFile);
            Path newFilePath = Paths.get(uploadPath, fileName);
            Files.copy(newFile.getInputStream(), newFilePath);

            image.setImagePath(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось обновить файл изображения", e);
        }
    }
}