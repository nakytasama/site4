package org.example.site4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
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
        return "profile";
    }
}

    // когда нибудь (завтра) возможно реализую, надо не забыть изменить начальную страницу:
    /*
    @GetMapping("/gallery")
    public String gallery() {
        return "gallery";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @GetMapping("/image/{id}")
    public String imageDetail(@PathVariable Long id, Model model) {
        // логика для детальной страницы изображения
        return "image-detail";
    }
    */
