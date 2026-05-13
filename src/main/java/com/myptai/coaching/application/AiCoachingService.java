package com.myptai.coaching.application;

import com.myptai.coaching.domain.AiCoachingRequest;
import com.myptai.coaching.repository.AiCoachingRequestRepository;
import com.myptai.user.application.CurrentUserService;
import com.myptai.user.domain.AppUser;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiCoachingService {

    private final AiCoachingRequestRepository aiCoachingRequestRepository;
    private final CurrentUserService currentUserService;
    private final CoachingPromptBuilder coachingPromptBuilder;
    private final OpenAiClient openAiClient;

    public AiCoachingService(
            AiCoachingRequestRepository aiCoachingRequestRepository,
            CurrentUserService currentUserService,
            CoachingPromptBuilder coachingPromptBuilder,
            OpenAiClient openAiClient
    ) {
        this.aiCoachingRequestRepository = aiCoachingRequestRepository;
        this.currentUserService = currentUserService;
        this.coachingPromptBuilder = coachingPromptBuilder;
        this.openAiClient = openAiClient;
    }

    @Transactional(readOnly = true)
    public List<AiCoachingView> findByDate(LocalDate targetDate) {
        return currentUserService.findCurrentUser()
                .map(user -> aiCoachingRequestRepository.findByUser_IdAndTargetDateOrderByCreatedAtDesc(
                                user.getId(),
                                targetDate
                        )
                        .stream()
                        .map(AiCoachingView::from)
                        .toList())
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public List<AiCoachingView> findRecentRequests() {
        return currentUserService.findCurrentUser()
                .map(user -> aiCoachingRequestRepository.findTop10ByUser_IdOrderByCreatedAtDesc(user.getId())
                        .stream()
                        .map(AiCoachingView::from)
                        .toList())
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public Optional<AiCoachingView> findRequest(Long requestId) {
        AppUser user = getCurrentUser();
        return aiCoachingRequestRepository.findByIdAndUser_Id(requestId, user.getId())
                .map(AiCoachingView::from);
    }

    @Transactional
    public AiCoachingView request(AiCoachingCommand command) {
        AppUser user = getCurrentUser();
        AiCoachingRequest request = AiCoachingRequest.request(
                user,
                command.targetDate(),
                command.question(),
                openAiClient.modelName()
        );
        aiCoachingRequestRepository.save(request);

        try {
            String prompt = coachingPromptBuilder.build(user, command);
            String answer = openAiClient.createCoachingAnswer(prompt);
            request.succeed(answer);
        } catch (OpenAiClientException exception) {
            request.fail(exception.getMessage());
        }

        return AiCoachingView.from(request);
    }

    private AppUser getCurrentUser() {
        return currentUserService.getCurrentUser(RequiredUserProfileException::new);
    }
}
