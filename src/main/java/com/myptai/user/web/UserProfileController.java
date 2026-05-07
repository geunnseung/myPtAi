package com.myptai.user.web;

import com.myptai.user.application.UserProfileService;
import com.myptai.user.domain.ActivityLevel;
import com.myptai.user.domain.GoalType;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @ModelAttribute("goalTypes")
    public GoalType[] goalTypes() {
        return GoalType.values();
    }

    @ModelAttribute("activityLevels")
    public ActivityLevel[] activityLevels() {
        return ActivityLevel.values();
    }

    @GetMapping
    public String profileForm(Model model) {
        if (!model.containsAttribute("form")) {
            UserProfileForm form = userProfileService.findCurrentProfile()
                    .map(UserProfileForm::from)
                    .orElseGet(UserProfileForm::empty);
            model.addAttribute("form", form);
        }

        return "user/profile";
    }

    @PostMapping
    public String saveProfile(
            @Valid @ModelAttribute("form") UserProfileForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "user/profile";
        }

        userProfileService.save(form.toCommand());
        redirectAttributes.addFlashAttribute("message", "프로필이 저장되었습니다.");
        return "redirect:/profile";
    }
}
