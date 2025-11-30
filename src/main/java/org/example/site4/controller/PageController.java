package org.example.site4.controller;

import org.example.site4.domain.Category;
import org.example.site4.domain.Image;
import org.example.site4.security.domain.User;
import org.example.site4.security.service.UserService;
import org.example.site4.service.ImageService;
import org.example.site4.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final ImageService imageService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        List<Image> images = imageService.getAllImages();
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("images", images);
        model.addAttribute("categories", categories);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = userService.getCurrentUser();
        List<Image> userImages = imageService.getUserImages(currentUser.getId());
        model.addAttribute("userImages", userImages);
        return "profile";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @GetMapping("/edit-image")
    public String editImage(@RequestParam Long id,
                            @RequestParam(required = false) String from,
                            Model model) {
        Image image = imageService.getImageById(id)
                .orElseThrow(() -> new RuntimeException("Изображение не найдено"));
        model.addAttribute("image", image);
        model.addAttribute("from", from);
        return "edit-image";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            List<Image> images = imageService.getAllImages();
            List<Category> categories = categoryService.getAllCategories();

            Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, Function.identity()));

            Map<Long, Long> categoryImageCounts = categories.stream()
                    .collect(Collectors.toMap(
                            Category::getId,
                            category -> (long) category.getImages().size()
                    ));

            User currentUser = userService.getCurrentUser();

            model.addAttribute("users", users);
            model.addAttribute("images", images);
            model.addAttribute("categories", categories);
            model.addAttribute("userMap", userMap);
            model.addAttribute("currentUserId", currentUser.getId());
            model.addAttribute("categoryImageCounts", categoryImageCounts);

            return "admin";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки данных: " + e.getMessage());
            return "admin";
        }
    }
}