package com.myptai.global.web;

import com.myptai.coaching.application.AiCoachingService;
import com.myptai.condition.application.DailyConditionService;
import com.myptai.global.time.CurrentDateProvider;
import com.myptai.meal.application.MealDailySummary;
import com.myptai.meal.application.MealRecordService;
import com.myptai.meal.application.MealRecordView;
import com.myptai.user.application.UserProfileService;
import com.myptai.workout.application.WorkoutDailySummary;
import com.myptai.workout.application.WorkoutRecordService;
import com.myptai.workout.application.WorkoutRecordView;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final UserProfileService userProfileService;
    private final MealRecordService mealRecordService;
    private final WorkoutRecordService workoutRecordService;
    private final DailyConditionService dailyConditionService;
    private final AiCoachingService aiCoachingService;
    private final CurrentDateProvider currentDateProvider;

    public HomeController(
            UserProfileService userProfileService,
            MealRecordService mealRecordService,
            WorkoutRecordService workoutRecordService,
            DailyConditionService dailyConditionService,
            AiCoachingService aiCoachingService,
            CurrentDateProvider currentDateProvider
    ) {
        this.userProfileService = userProfileService;
        this.mealRecordService = mealRecordService;
        this.workoutRecordService = workoutRecordService;
        this.dailyConditionService = dailyConditionService;
        this.aiCoachingService = aiCoachingService;
        this.currentDateProvider = currentDateProvider;
    }

    @GetMapping("/")
    public String home(Model model, RedirectAttributes redirectAttributes) {
        if (userProfileService.findCurrentProfile().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "먼저 프로필을 등록해 주세요.");
            return "redirect:/profile";
        }

        LocalDate today = currentDateProvider.today();
        List<MealRecordView> meals = mealRecordService.findByDate(today);
        List<WorkoutRecordView> workouts = workoutRecordService.findByDate(today);

        model.addAttribute("today", today);
        model.addAttribute("mealSummary", MealDailySummary.from(meals));
        model.addAttribute("mealCount", meals.size());
        model.addAttribute("workoutSummary", WorkoutDailySummary.from(workouts));
        model.addAttribute("condition", dailyConditionService.findByDate(today).orElse(null));
        model.addAttribute("recentCoaching", aiCoachingService.findRecentRequests());
        return "home/dashboard";
    }
}
