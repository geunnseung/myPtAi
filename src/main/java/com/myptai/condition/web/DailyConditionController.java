package com.myptai.condition.web;

import com.myptai.condition.application.DailyConditionNotFoundException;
import com.myptai.condition.application.DailyConditionService;
import com.myptai.condition.application.RequiredUserProfileException;
import com.myptai.condition.domain.EnergyLevel;
import com.myptai.global.time.CurrentDateProvider;
import com.myptai.user.application.UserProfileService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/conditions")
public class DailyConditionController {

    private final DailyConditionService dailyConditionService;
    private final UserProfileService userProfileService;
    private final CurrentDateProvider currentDateProvider;

    public DailyConditionController(
            DailyConditionService dailyConditionService,
            UserProfileService userProfileService,
            CurrentDateProvider currentDateProvider
    ) {
        this.dailyConditionService = dailyConditionService;
        this.userProfileService = userProfileService;
        this.currentDateProvider = currentDateProvider;
    }

    @ModelAttribute("energyLevels")
    public EnergyLevel[] energyLevels() {
        return EnergyLevel.values();
    }

    @GetMapping
    public String conditionForm(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (userProfileService.findCurrentProfile().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "컨디션 기록을 사용하려면 프로필을 먼저 등록해 주세요.");
            return "redirect:/profile";
        }

        LocalDate recordedOn = defaultDate(date);
        if (!model.containsAttribute("form")) {
            DailyConditionForm form = dailyConditionService.findByDate(recordedOn)
                    .map(DailyConditionForm::from)
                    .orElseGet(() -> DailyConditionForm.empty(recordedOn));
            model.addAttribute("form", form);
        }
        model.addAttribute("recordedOn", recordedOn);
        addDateNavigationAttributes(model, recordedOn);
        model.addAttribute("condition", dailyConditionService.findByDate(recordedOn).orElse(null));
        return "condition/form";
    }

    @PostMapping
    public String save(
            @Valid @ModelAttribute("form") DailyConditionForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            LocalDate recordedOn = defaultDate(form.getRecordedOn());
            model.addAttribute("recordedOn", recordedOn);
            addDateNavigationAttributes(model, recordedOn);
            model.addAttribute("condition", dailyConditionService.findByDate(recordedOn).orElse(null));
            return "condition/form";
        }

        dailyConditionService.save(form.toCommand());
        redirectAttributes.addFlashAttribute("message", "컨디션 기록이 저장되었습니다.");
        return "redirect:/conditions?date=" + form.getRecordedOn();
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam(name = "date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            RedirectAttributes redirectAttributes
    ) {
        dailyConditionService.delete(date);
        redirectAttributes.addFlashAttribute("message", "컨디션 기록이 삭제되었습니다.");
        return "redirect:/conditions?date=" + date;
    }

    @GetMapping("/delete")
    public String deleteConfirm(
            @RequestParam(name = "date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        model.addAttribute("condition", dailyConditionService.findByDate(date)
                .orElseThrow(() -> new DailyConditionNotFoundException(date)));
        return "condition/delete";
    }

    @ExceptionHandler(RequiredUserProfileException.class)
    public String handleRequiredUserProfile(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "컨디션 기록을 사용하려면 프로필을 먼저 등록해 주세요.");
        return "redirect:/profile";
    }

    @ExceptionHandler(DailyConditionNotFoundException.class)
    public String handleConditionNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "컨디션 기록을 찾을 수 없습니다.");
        return "redirect:/conditions";
    }

    private LocalDate defaultDate(LocalDate date) {
        return date == null ? currentDateProvider.today() : date;
    }

    private void addDateNavigationAttributes(Model model, LocalDate date) {
        model.addAttribute("previousDate", date.minusDays(1));
        model.addAttribute("today", currentDateProvider.today());
        model.addAttribute("nextDate", date.plusDays(1));
    }
}
