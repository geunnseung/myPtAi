package com.myptai.coaching.web;

import com.myptai.coaching.application.AiCoachingService;
import com.myptai.coaching.application.AiCoachingView;
import com.myptai.coaching.application.RequiredUserProfileException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coach")
public class AiCoachingController {

    private final AiCoachingService aiCoachingService;
    private final UserProfileService userProfileService;

    public AiCoachingController(AiCoachingService aiCoachingService, UserProfileService userProfileService) {
        this.aiCoachingService = aiCoachingService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public String coachingForm(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "requestId", required = false) Long requestId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (userProfileService.findCurrentProfile().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "AI 코칭을 사용하려면 프로필을 먼저 등록해 주세요.");
            return "redirect:/profile";
        }

        LocalDate targetDate = defaultDate(date);
        List<AiCoachingView> requests = aiCoachingService.findByDate(targetDate);
        AiCoachingView selectedRequest = findSelectedRequest(requestId, requests);

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", AiCoachingForm.empty(targetDate));
        }
        addPageAttributes(model, targetDate, requests, selectedRequest);
        return "coaching/form";
    }

    @PostMapping
    public String requestCoaching(
            @Valid @ModelAttribute("form") AiCoachingForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        LocalDate targetDate = defaultDate(form.getTargetDate());
        if (bindingResult.hasErrors()) {
            List<AiCoachingView> requests = aiCoachingService.findByDate(targetDate);
            addPageAttributes(model, targetDate, requests, requests.stream().findFirst().orElse(null));
            return "coaching/form";
        }

        AiCoachingView coaching = aiCoachingService.request(form.toCommand());
        if (coaching.succeeded()) {
            redirectAttributes.addFlashAttribute("message", "AI 코칭 답변이 생성되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "AI 코칭 요청이 저장되었지만 응답 생성에 실패했습니다.");
        }
        return "redirect:/coach?date=" + coaching.targetDate() + "&requestId=" + coaching.id();
    }

    @GetMapping("/history")
    public String coachingHistory(Model model, RedirectAttributes redirectAttributes) {
        if (userProfileService.findCurrentProfile().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "AI 코칭 이력을 보려면 프로필을 먼저 등록해 주세요.");
            return "redirect:/profile";
        }

        model.addAttribute("historyRequests", aiCoachingService.findHistoryRequests());
        return "coaching/history";
    }

    @ExceptionHandler(RequiredUserProfileException.class)
    public String handleRequiredUserProfile(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "AI 코칭을 사용하려면 프로필을 먼저 등록해 주세요.");
        return "redirect:/profile";
    }

    private AiCoachingView findSelectedRequest(Long requestId, List<AiCoachingView> requests) {
        if (requestId == null) {
            return requests.stream().findFirst().orElse(null);
        }
        return aiCoachingService.findRequest(requestId)
                .orElseGet(() -> requests.stream().findFirst().orElse(null));
    }

    private void addPageAttributes(
            Model model,
            LocalDate targetDate,
            List<AiCoachingView> requests,
            AiCoachingView selectedRequest
    ) {
        model.addAttribute("targetDate", targetDate);
        model.addAttribute("contextPreview", aiCoachingService.previewContext(targetDate));
        model.addAttribute("requests", requests);
        model.addAttribute("selectedRequest", selectedRequest);
        model.addAttribute("recentRequests", aiCoachingService.findRecentRequests());
    }

    private LocalDate defaultDate(LocalDate date) {
        return date == null ? LocalDate.now() : date;
    }
}
