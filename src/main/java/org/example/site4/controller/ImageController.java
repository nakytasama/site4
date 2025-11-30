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
import java.util.Map;

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

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateImage(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        System.out.println("=== UPDATE IMAGE METHOD CALLED ===");
        System.out.println("ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Category ID: " + categoryId);
        System.out.println("File: " + (file != null ? file.getOriginalFilename() : "null"));

        try {
            Image imageDetails = new Image();
            imageDetails.setTitle(title);
            imageDetails.setDescription(description);

            Image updatedImage = imageService.updateImage(id, imageDetails, file, categoryId);
            return ResponseEntity.ok(updatedImage);
        } catch (RuntimeException e) {
            System.out.println("Error in updateImage: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
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