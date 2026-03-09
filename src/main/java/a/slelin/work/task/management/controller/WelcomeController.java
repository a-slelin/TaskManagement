package a.slelin.work.task.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WelcomeController {

    @ModelAttribute("title")
    public String title() {
        return "Welcome to Task Management System!!!";
    }

    @GetMapping
    public String welcome() {
        return "welcome";
    }
}
