package org.example.site4.controller;

import lombok.RequiredArgsConstructor;
import org.example.site4.domain.Image;
import org.example.site4.security.domain.User;
import org.example.site4.security.service.UserService;
import org.example.site4.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final UserService userService;

    @GetMapping
    public List<Image> getAllImages() {
        return imageService.getAllImages();
    }

    @GetMapping("/user/{userId}")
    public List<Image> getUserImages(@PathVariable Long userId) {
        return imageService.getUserImages(userId);
    }

    @GetMapping("/category/{categoryId}")
    public List<Image> getImagesByCategory(@PathVariable Long categoryId) {
        return imageService.getImagesByCategory(categoryId);
    }

    @GetMapping("/search")
    public List<Image> searchImages(@RequestParam String query) {
        return imageService.searchImages(query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return imageService.getImageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Image uploadImage(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam("file") MultipartFile file) {

        User currentUser = userService.getCurrentUser();
        Image image = new Image();
        image.setTitle(title);
        image.setDescription(description);

        return imageService.saveImage(image, file, currentUser, categoryId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Image> updateImage(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            Image imageDetails = new Image();
            if (title != null) imageDetails.setTitle(title);
            if (description != null) imageDetails.setDescription(description);

            Image updatedImage = imageService.updateImage(id, imageDetails, file, categoryId);
            return ResponseEntity.ok(updatedImage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent")
    public List<Image> getRecentImages(@RequestParam(defaultValue = "10") int count) {
        return imageService.getRecentImages(count);
    }
}