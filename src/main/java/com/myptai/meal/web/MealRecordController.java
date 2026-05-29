package com.myptai.meal.web;

import com.myptai.global.time.CurrentDateProvider;
import com.myptai.meal.application.MealDailySummary;
import com.myptai.meal.application.MealNutritionEstimateCommand;
import com.myptai.meal.application.MealNutritionEstimateException;
import com.myptai.meal.application.MealNutritionEstimateService;
import com.myptai.meal.application.MealNutritionEstimateView;
import com.myptai.meal.application.MealRecordNotFoundException;
import com.myptai.meal.application.MealRecordService;
import com.myptai.meal.application.MealRecordView;
import com.myptai.meal.application.RequiredUserProfileException;
import com.myptai.meal.domain.MealType;
import com.myptai.user.application.UserProfileService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/meals")
public class MealRecordController {

    private final MealRecordService mealRecordService;
    private final MealNutritionEstimateService mealNutritionEstimateService;
    private final UserProfileService userProfileService;
    private final CurrentDateProvider currentDateProvider;

    public MealRecordController(
            MealRecordService mealRecordService,
            MealNutritionEstimateService mealNutritionEstimateService,
            UserProfileService userProfileService,
            CurrentDateProvider currentDateProvider
    ) {
        this.mealRecordService = mealRecordService;
        this.mealNutritionEstimateService = mealNutritionEstimateService;
        this.userProfileService = userProfileService;
        this.currentDateProvider = currentDateProvider;
    }

    @ModelAttribute("mealTypes")
    public MealType[] mealTypes() {
        return MealType.values();
    }

    @GetMapping
    public String mealList(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (userProfileService.findCurrentProfile().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "식단 기록을 사용하려면 프로필을 먼저 등록해 주세요.");
            return "redirect:/profile";
        }

        LocalDate recordedOn = defaultDate(date);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", MealRecordForm.empty(recordedOn));
        }
        addListAttributes(model, recordedOn);
        return "meal/list";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") MealRecordForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addListAttributes(model, defaultDate(form.getRecordedOn()));
            return "meal/list";
        }

        mealRecordService.create(form.toCommand());
        redirectAttributes.addFlashAttribute("message", "식단 기록이 추가되었습니다.");
        return "redirect:/meals?date=" + form.getRecordedOn();
    }

    @PostMapping("/nutrition-estimate")
    public String estimateNutrition(
            @ModelAttribute("form") MealRecordForm form,
            BindingResult bindingResult,
            Model model
    ) {
        applyNutritionEstimate(form, bindingResult, model);
        addListAttributes(model, defaultDate(form.getRecordedOn()));
        return "meal/list";
    }

    @GetMapping("/{mealRecordId}/edit")
    public String editForm(@PathVariable Long mealRecordId, Model model) {
        MealRecordView mealRecord = mealRecordService.getForEdit(mealRecordId);
        model.addAttribute("mealRecordId", mealRecordId);
        model.addAttribute("form", MealRecordForm.from(mealRecord));
        return "meal/form";
    }

    @GetMapping("/{mealRecordId}/delete")
    public String deleteConfirm(@PathVariable Long mealRecordId, Model model) {
        MealRecordView mealRecord = mealRecordService.getForEdit(mealRecordId);
        model.addAttribute("mealRecord", mealRecord);
        return "meal/delete";
    }

    @PostMapping("/{mealRecordId}")
    public String update(
            @PathVariable Long mealRecordId,
            @Valid @ModelAttribute("form") MealRecordForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mealRecordId", mealRecordId);
            return "meal/form";
        }

        mealRecordService.update(mealRecordId, form.toCommand());
        redirectAttributes.addFlashAttribute("message", "식단 기록이 수정되었습니다.");
        return "redirect:/meals?date=" + form.getRecordedOn();
    }

    @PostMapping("/{mealRecordId}/nutrition-estimate")
    public String estimateNutritionForEdit(
            @PathVariable Long mealRecordId,
            @ModelAttribute("form") MealRecordForm form,
            BindingResult bindingResult,
            Model model
    ) {
        model.addAttribute("mealRecordId", mealRecordId);
        applyNutritionEstimate(form, bindingResult, model);
        return "meal/form";
    }

    @PostMapping("/{mealRecordId}/delete")
    public String delete(
            @PathVariable Long mealRecordId,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            RedirectAttributes redirectAttributes
    ) {
        mealRecordService.delete(mealRecordId);
        redirectAttributes.addFlashAttribute("message", "식단 기록이 삭제되었습니다.");
        return "redirect:/meals?date=" + defaultDate(date);
    }

    @ExceptionHandler(RequiredUserProfileException.class)
    public String handleRequiredUserProfile(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "식단 기록을 사용하려면 프로필을 먼저 등록해 주세요.");
        return "redirect:/profile";
    }

    @ExceptionHandler(MealRecordNotFoundException.class)
    public String handleMealRecordNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "식단 기록을 찾을 수 없습니다.");
        return "redirect:/meals";
    }

    private void addListAttributes(Model model, LocalDate recordedOn) {
        List<MealRecordView> meals = mealRecordService.findByDate(recordedOn);
        model.addAttribute("recordedOn", recordedOn);
        addDateNavigationAttributes(model, recordedOn);
        model.addAttribute("meals", meals);
        model.addAttribute("summary", MealDailySummary.from(meals));
    }

    private void addDateNavigationAttributes(Model model, LocalDate date) {
        model.addAttribute("previousDate", date.minusDays(1));
        model.addAttribute("today", currentDateProvider.today());
        model.addAttribute("nextDate", date.plusDays(1));
    }

    private void applyNutritionEstimate(MealRecordForm form, BindingResult bindingResult, Model model) {
        try {
            MealNutritionEstimateView estimate = mealNutritionEstimateService.estimate(
                    new MealNutritionEstimateCommand(form.getNutritionEstimateInput())
            );
            form.applyNutritionEstimate(estimate);
            model.addAttribute("nutritionEstimateMessage", "AI 계산 결과를 입력칸에 반영했습니다. 저장 전 확인해 주세요.");
        } catch (MealNutritionEstimateException exception) {
            bindingResult.rejectValue("nutritionEstimateInput", "nutritionEstimate", exception.getMessage());
        }
    }

    private LocalDate defaultDate(LocalDate date) {
        return date == null ? currentDateProvider.today() : date;
    }
}
