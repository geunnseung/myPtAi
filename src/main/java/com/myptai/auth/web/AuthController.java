package com.myptai.auth.web;

import com.myptai.auth.application.DuplicateEmailException;
import com.myptai.auth.application.SignupService;
import com.myptai.user.domain.GoalType;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final SignupService signupService;

    public AuthController(SignupService signupService) {
        this.signupService = signupService;
    }

    @ModelAttribute("goalTypes")
    public GoalType[] goalTypes() {
        return GoalType.values();
    }

    @GetMapping("/login")
    public String loginForm(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
            @RequestParam(name = "registered", required = false) String registered,
            Model model
    ) {
        model.addAttribute("loginError", error != null);
        model.addAttribute("loggedOut", logout != null);
        model.addAttribute("registered", registered != null);
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", SignupForm.empty());
        }
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("form") SignupForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            signupService.signup(form.toCommand());
        } catch (DuplicateEmailException exception) {
            bindingResult.rejectValue("email", "duplicate", exception.getMessage());
            return "auth/signup";
        }

        redirectAttributes.addAttribute("registered", true);
        return "redirect:/login";
    }
}
