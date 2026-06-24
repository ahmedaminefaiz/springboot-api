package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.intervention.CreateInterventionRequestDTO;
import org.urban.alert.dto.intervention.CreateInterventionUpdateRequestDTO;
import org.urban.alert.dto.intervention.InterventionResponseDTO;
import org.urban.alert.dto.intervention.InterventionUpdateResponseDTO;
import org.urban.alert.entity.Intervention;
import org.urban.alert.entity.InterventionUpdate;
import org.urban.alert.entity.Problem;
import org.urban.alert.entity.User;
import org.urban.alert.entity.enums.InterventionStatusEnum;
import org.urban.alert.entity.enums.RoleEnum;
import org.urban.alert.exception.UserNotFoundException;
import org.urban.alert.exception.intervention.InterventionCannotBeModifiedException;
import org.urban.alert.exception.intervention.InterventionNotFoundException;
import org.urban.alert.exception.problem.InvalidProblemException;
import org.urban.alert.exception.problem.NotAgentException;
import org.urban.alert.exception.problem.ProblemNotFoundException;
import org.urban.alert.repository.InterventionRepository;
import org.urban.alert.repository.InterventionUpdateRepository;
import org.urban.alert.repository.ProblemRepository;
import org.urban.alert.repository.UserRepository;
import org.urban.alert.service.InterventionService;
import org.urban.alert.service.NotificationService;
import org.urban.alert.service.mapper.InterventionMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InterventionServiceImpl implements InterventionService {

    private final InterventionRepository interventionRepository;
    private final InterventionUpdateRepository interventionUpdateRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final InterventionMapper interventionMapper;
    private final NotificationService notificationService;

    private static final Set<InterventionStatusEnum> DURATION_TRIGGER_STATUSES = Set.of(
            InterventionStatusEnum.RESOLUE,
            InterventionStatusEnum.PARTIELLEMENT_RESOLUE,
            InterventionStatusEnum.ECHEC_INTERVENTION
    );

    @Override
    public InterventionResponseDTO createIntervention(CreateInterventionRequestDTO request, Long superAgentId) {
        log.info("Creating intervention on problem {} for agent {}", request.getProblemId(), request.getAgentId());

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ProblemNotFoundException(request.getProblemId()));

        User agent = userRepository.findById(request.getAgentId())
                .orElseThrow(() -> new UserNotFoundException(request.getAgentId()));

        if (agent.getRole() != RoleEnum.AGENT) {
            throw new NotAgentException(request.getAgentId());
        }

        Intervention intervention = Intervention.builder()
                .problem(problem)
                .agent(agent)
                .description(request.getDescription())
                .actionType(request.getActionType())
                .photos(request.getPhotos() != null ? new ArrayList<>(request.getPhotos()) : new ArrayList<>())
                .build();

        Intervention saved = interventionRepository.save(intervention);
        log.info("Intervention created with ID: {}", saved.getId());

        notificationService.notifyAgentInterventionAssigned(agent, problem.getTitle(), saved.getId());

        return interventionMapper.entityToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InterventionResponseDTO> getMyInterventions(Long agentId, Pageable pageable) {
        log.info("Fetching interventions for agent: {}", agentId);
        return interventionRepository.findByAgentId(agentId, pageable)
                .map(interventionMapper::entityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InterventionResponseDTO> getByProblem(Long problemId, Long userId, String userRole, Pageable pageable) {
        log.info("Fetching interventions for problem: {}", problemId);

        if (!problemRepository.existsById(problemId)) {
            throw new ProblemNotFoundException(problemId);
        }

        if (userRole.contains("AGENT") && !userRole.contains("SUPER_AGENT")) {
            if (!interventionRepository.existsByProblemIdAndAgentId(problemId, userId)) {
                throw new SecurityException("Accès refusé : vous n'avez aucune intervention sur ce problème");
            }
        }

        return interventionRepository.findByProblemId(problemId, pageable)
                .map(interventionMapper::entityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InterventionResponseDTO getById(Long id, Long userId, String userRole) {
        log.info("Fetching intervention: {}", id);

        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new InterventionNotFoundException(id));

        if (userRole.contains("AGENT") && !userRole.contains("SUPER_AGENT")) {
            if (!interventionRepository.existsByProblemIdAndAgentId(intervention.getProblem().getId(), userId)) {
                throw new SecurityException("Accès refusé");
            }
        }

        return interventionMapper.entityToResponse(intervention);
    }

    @Override
    public InterventionResponseDTO updateIntervention(Long id, CreateInterventionUpdateRequestDTO request, Long agentId) {
        return applyUpdate(id, request, agentId);
    }

    @Override
    public InterventionResponseDTO createInterventionUpdate(Long id, CreateInterventionUpdateRequestDTO request, Long agentId) {
        return applyUpdate(id, request, agentId);
    }

    private InterventionResponseDTO applyUpdate(Long id, CreateInterventionUpdateRequestDTO request, Long agentId) {
        log.info("Agent {} updating intervention {}", agentId, id);

        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new InterventionNotFoundException(id));

        if (!intervention.getAgent().getId().equals(agentId)) {
            throw new SecurityException("Vous ne pouvez modifier que vos propres interventions");
        }

        if (intervention.isCloturee()) {
            throw new InterventionCannotBeModifiedException(id);
        }

        if (request.getStatus() == InterventionStatusEnum.CLOTUREE) {
            throw new InvalidProblemException("L'agent ne peut pas clôturer une intervention");
        }

        InterventionUpdate update = InterventionUpdate.builder()
                .intervention(intervention)
                .rapport(request.getRapport())
                .status(request.getStatus())
                .photos(request.getPhotos() != null ? new ArrayList<>(request.getPhotos()) : new ArrayList<>())
                .build();

        interventionUpdateRepository.save(update);

        intervention.setStatus(request.getStatus());

        if (intervention.getDuration() == null && DURATION_TRIGGER_STATUSES.contains(request.getStatus())) {
            intervention.setDuration((int) ChronoUnit.MINUTES.between(intervention.getInterventionDate(), LocalDateTime.now()));
        }

        Intervention updated = interventionRepository.save(intervention);
        log.info("Intervention {} updated to status {}", id, request.getStatus());
        return interventionMapper.entityToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterventionUpdateResponseDTO> getInterventionUpdates(Long interventionId, Long userId, String userRole) {
        log.info("Fetching updates for intervention: {}", interventionId);

        Intervention intervention = interventionRepository.findById(interventionId)
                .orElseThrow(() -> new InterventionNotFoundException(interventionId));

        if (userRole.contains("AGENT") && !userRole.contains("SUPER_AGENT")) {
            if (!interventionRepository.existsByProblemIdAndAgentId(intervention.getProblem().getId(), userId)) {
                throw new SecurityException("Accès refusé");
            }
        }

        return interventionUpdateRepository.findByInterventionIdOrderByCreatedAtAsc(interventionId)
                .stream()
                .map(interventionMapper::updateEntityToResponse)
                .toList();
    }

    @Override
    public InterventionResponseDTO addPhoto(Long id, String photoUrl, Long superAgentId) {
        log.info("Super agent adding photo to intervention: {}", id);

        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new InterventionNotFoundException(id));

        if (intervention.getPhotos().size() >= 3) {
            throw new InvalidProblemException("Maximum 3 photos par intervention");
        }

        intervention.getPhotos().add(photoUrl);
        return interventionMapper.entityToResponse(interventionRepository.save(intervention));
    }

    @Override
    public InterventionResponseDTO removePhoto(Long id, String photoUrl, Long superAgentId) {
        log.info("Super agent removing photo from intervention: {}", id);

        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new InterventionNotFoundException(id));

        if (!intervention.getPhotos().remove(photoUrl)) {
            throw new InvalidProblemException("La photo n'a pas été trouvée dans l'intervention");
        }

        return interventionMapper.entityToResponse(interventionRepository.save(intervention));
    }

    @Override
    public InterventionResponseDTO closeIntervention(Long id, Long superAgentId) {
        log.info("Super agent {} closing intervention {}", superAgentId, id);

        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() -> new InterventionNotFoundException(id));

        if (intervention.isCloturee()) {
            throw new InterventionCannotBeModifiedException(id);
        }

        intervention.setStatus(InterventionStatusEnum.CLOTUREE);
        Intervention updated = interventionRepository.save(intervention);
        log.info("Intervention {} closed", id);
        return interventionMapper.entityToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            String phone = authentication.getName();
            return userRepository.findByPhone(phone)
                    .map(User::getId)
                    .orElseThrow(() -> new UserNotFoundException(null));
        }
        throw new UserNotFoundException(null);
    }
}
