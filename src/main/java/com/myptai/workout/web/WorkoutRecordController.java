package com.myptai.workout.web;

import com.myptai.user.application.UserProfileService;
import com.myptai.workout.application.RequiredUserProfileException;
import com.myptai.workout.application.WorkoutDailySummary;
import com.myptai.workout.application.WorkoutRecordNotFoundException;
import com.myptai.workout.application.WorkoutRecordService;
import com.myptai.workout.application.WorkoutRecordView;
import com.myptai.workout.domain.WorkoutIntensity;
import com.myptai.workout.domain.WorkoutType;
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
@RequestMapping("/workouts")
public class WorkoutRecordController {

    private final WorkoutRecordService workoutRecordService;
    private final UserProfileService userProfileService;

    public WorkoutRecordController(WorkoutRecordService workoutRecordService, UserProfileService userProfileService) {
        this.workoutRecordService = workoutRecordService;
        this.userProfileService = userProfileService;
    }

    @ModelAttribute("workoutTypes")
    public WorkoutType[] workoutTypes() {
        return WorkoutType.values();
    }

    @ModelAttribute("workoutIntensities")
    public WorkoutIntensity[] workoutIntensities() {
        return WorkoutIntensity.values();
    }

    @GetMapping
    public String workoutList(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (userProfileService.findCurrentProfile().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "운동 기록을 사용하려면 프로필을 먼저 등록해 주세요.");
            return "redirect:/profile";
        }

        LocalDate recordedOn = defaultDate(date);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", WorkoutRecordForm.empty(recordedOn));
        }
        addListAttributes(model, recordedOn);
        return "workout/list";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") WorkoutRecordForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addListAttributes(model, defaultDate(form.getRecordedOn()));
            return "workout/list";
        }

        workoutRecordService.create(form.toCommand());
        redirectAttributes.addFlashAttribute("message", "운동 기록이 추가되었습니다.");
        return "redirect:/workouts?date=" + form.getRecordedOn();
    }

    @GetMapping("/{workoutRecordId}/edit")
    public String editForm(@PathVariable Long workoutRecordId, Model model) {
        WorkoutRecordView workoutRecord = workoutRecordService.getForEdit(workoutRecordId);
        model.addAttribute("workoutRecordId", workoutRecordId);
        model.addAttribute("form", WorkoutRecordForm.from(workoutRecord));
        return "workout/form";
    }

    @PostMapping("/{workoutRecordId}")
    public String update(
            @PathVariable Long workoutRecordId,
            @Valid @ModelAttribute("form") WorkoutRecordForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("workoutRecordId", workoutRecordId);
            return "workout/form";
        }

        workoutRecordService.update(workoutRecordId, form.toCommand());
        redirectAttributes.addFlashAttribute("message", "운동 기록이 수정되었습니다.");
        return "redirect:/workouts?date=" + form.getRecordedOn();
    }

    @PostMapping("/{workoutRecordId}/delete")
    public String delete(
            @PathVariable Long workoutRecordId,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            RedirectAttributes redirectAttributes
    ) {
        workoutRecordService.delete(workoutRecordId);
        redirectAttributes.addFlashAttribute("message", "운동 기록이 삭제되었습니다.");
        return "redirect:/workouts?date=" + defaultDate(date);
    }

    @ExceptionHandler(RequiredUserProfileException.class)
    public String handleRequiredUserProfile(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "운동 기록을 사용하려면 프로필을 먼저 등록해 주세요.");
        return "redirect:/profile";
    }

    @ExceptionHandler(WorkoutRecordNotFoundException.class)
    public String handleWorkoutRecordNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "운동 기록을 찾을 수 없습니다.");
        return "redirect:/workouts";
    }

    private void addListAttributes(Model model, LocalDate recordedOn) {
        List<WorkoutRecordView> workouts = workoutRecordService.findByDate(recordedOn);
        model.addAttribute("recordedOn", recordedOn);
        model.addAttribute("workouts", workouts);
        model.addAttribute("summary", WorkoutDailySummary.from(workouts));
    }

    private LocalDate defaultDate(LocalDate date) {
        return date == null ? LocalDate.now() : date;
    }
}
