package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.problem.*;
import org.urban.alert.entity.*;
import org.urban.alert.entity.enums.*;
import org.urban.alert.exception.UserNotFoundException;
import org.urban.alert.exception.alert.AlertNotFoundException;
import org.urban.alert.exception.problem.*;
import org.urban.alert.repository.*;
import org.urban.alert.service.NotificationService;
import org.urban.alert.service.ProblemService;
import org.urban.alert.service.mapper.ProblemMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final ProblemStatusHistoryRepository historyRepository;
    private final ProblemMapper problemMapper;
    private final NotificationService notificationService;

    // ========== CRUD Operations ==========

    @Override
    public ProblemResponseDTO createProblem(ProblemCreateDTO request, Long createdByUserId) {
        log.info("Creating new problem for SuperAgent: {}", createdByUserId);

        //  Vérifier que l'utilisateur qui crée est un SUPER_AGENT
        User creator = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new UserNotFoundException(createdByUserId));

        verifySuperAgentRole(createdByUserId);

        //  Vérifier que assigned_to est un AGENT
        User assignedAgent = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new UserNotFoundException(request.getAssignedToId()));

        verifyAgentRole(request.getAssignedToId());

        //  Vérifier qu'au moins une alerte est spécifiée
        if (request.getAlertIds() == null || request.getAlertIds().isEmpty()) {
            throw new NoAlertsAssignedException();
        }

        // Créer le problème
        Problem problem = Problem.builder()
                .user(creator)
                .assignedTo(assignedAgent)
                .status(ProblemStatusEnum.NEW)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        Problem savedProblem = problemRepository.save(problem);
        log.info("Problem created with ID: {}", savedProblem.getId());

        // Ajouter les alertes et changer leur statut
        for (Long alertId : request.getAlertIds()) {
            addAlertToProblem(savedProblem.getId(), alertId);
        }

        // Ajouter à l'historique des statuts
        ProblemStatusHistory history = ProblemStatusHistory.builder()
                .problem(savedProblem)
                .previousStatus(null)
                .newStatus(ProblemStatusEnum.NEW)
                .changedBy(creator.getPhone())
                .comment("Problème créé")
                .build();
        historyRepository.save(history);

        //  Notifier les citoyens
        notifyAlertCreators(savedProblem.getId(), "NEW");

        return problemMapper.entityToProblemResponse(savedProblem);
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemResponseDTO getProblemById(Long id) {
        log.info("Fetching problem with ID: {}", id);

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));

        return problemMapper.entityToProblemResponse(problem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemResponseDTO> getAllProblems(Pageable pageable) {
        log.info("Fetching all problems with pagination");

        return problemRepository.findAll(pageable)
                .map(problemMapper::entityToProblemResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemResponseDTO> getProblemsCreatedBy(Long userId, Pageable pageable) {
        log.info("Fetching problems created by user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return problemRepository.findByUserId(userId, pageable)
                .map(problemMapper::entityToProblemResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemResponseDTO> getProblemsAssignedTo(Long agentId, Pageable pageable) {
        log.info("Fetching problems assigned to agent: {}", agentId);

        if (!userRepository.existsById(agentId)) {
            throw new UserNotFoundException(agentId);
        }

        return problemRepository.findByAssignedToId(agentId, pageable)
                .map(problemMapper::entityToProblemResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemResponseDTO> getProblemsByStatus(ProblemStatusEnum status, Pageable pageable) {
        log.info("Fetching problems by status: {}", status);

        return problemRepository.findByStatus(status, pageable)
                .map(problemMapper::entityToProblemResponse);
    }

    @Override
    public ProblemResponseDTO updateProblem(Long id, ProblemUpdateDTO request, Long userId) {
        log.info("Updating problem with ID: {} for user: {}", id, userId);

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));

        // Vérifier les permissions
        verifyCanModifyProblem(id, userId);

        // Vérifier que le problème peut être modifié
        if (!problem.canBeModified()) {
            throw new ProblemCannotBeModifiedException(id);
        }

        if (request.getTitle() != null) {
            problem.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            problem.setDescription(request.getDescription());
        }

        // Changer l'agent assigné si demandé
        if (request.getAssignedToId() != null && 
            !request.getAssignedToId().equals(problem.getAssignedTo().getId())) {
            User newAgent = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new UserNotFoundException(request.getAssignedToId()));
            verifyAgentRole(request.getAssignedToId());
            problem.setAssignedTo(newAgent);
        }

        // Ajouter des alertes si demandé
        if (request.getAddAlertIds() != null && !request.getAddAlertIds().isEmpty()) {
            for (Long alertId : request.getAddAlertIds()) {
                addAlertToProblem(id, alertId);
            }
        }

        // Retirer des alertes si demandé
        if (request.getRemoveAlertIds() != null && !request.getRemoveAlertIds().isEmpty()) {
            for (Long alertId : request.getRemoveAlertIds()) {
                removeAlertFromProblem(id, alertId);
            }
        }

        Problem updatedProblem = problemRepository.save(problem);
        log.info("Problem updated with ID: {}", updatedProblem.getId());

        return problemMapper.entityToProblemResponse(updatedProblem);
    }

    @Override
    public void deleteProblem(Long id, Long userId) {
        log.info("Deleting problem with ID: {} for user: {}", id, userId);

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));

        // Vérifier que c'est le SUPER_AGENT créateur
        if (!problem.getUser().getId().equals(userId)) {
            throw new InvalidProblemException("Seul le créateur du problème peut le supprimer");
        }

        verifySuperAgentRole(userId);

        // Vérifier que le problème peut être supprimé
        if (!problem.canBeModified()) {
            throw new ProblemCannotBeModifiedException(id);
        }

        boolean resetAlertsToNew = ProblemStatusEnum.IN_PROGRESS.equals(problem.getStatus());
        boolean resetAlertsToNew1 = ProblemStatusEnum.NEW.equals(problem.getStatus());
        for (Alert alert : new ArrayList<>(problem.getAlerts())) {
            problem.removeAlert(alert);
            if (resetAlertsToNew || resetAlertsToNew1) {
                alert.setStatus(AlertStatusEnum.NEW);
            }
            alertRepository.save(alert);
        }

        problemRepository.delete(problem);
        log.info("Problem deleted with ID: {}", id);
    }

    // ========== Alert Management ==========

    @Override
    public ProblemResponseDTO addAlertToProblem(Long problemId, Long alertId) {
        log.info("Adding alert {} to problem {}", alertId, problemId);

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(problemId));

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));

        // Vérifier que l'alerte n'est pas déjà assignée
        if (alert.getProblem() != null) {
            throw new AlertAlreadyAssignedException(alertId);
        }

        // Assigner l'alerte au problème
        problem.addAlert(alert);
        alert.setProblem(problem);

        // Changer le statut de l'alerte en IN_PROGRESS
        alert.setStatus(AlertStatusEnum.IN_PROGRESS);

        alertRepository.save(alert);
        Problem updatedProblem = problemRepository.save(problem);

        log.info("Alert added to problem");
        return problemMapper.entityToProblemResponse(updatedProblem);
    }

    @Override
    public ProblemResponseDTO removeAlertFromProblem(Long problemId, Long alertId) {
        log.info("Removing alert {} from problem {}", alertId, problemId);

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(problemId));

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));

        // Retirer l'alerte du problème
        problem.removeAlert(alert);

        // Changer le statut de l'alerte en NEW
        alert.setStatus(AlertStatusEnum.NEW);

        alertRepository.save(alert);
        Problem updatedProblem = problemRepository.save(problem);

        log.info("Alert removed from problem");
        return problemMapper.entityToProblemResponse(updatedProblem);
    }

    @Override
    @Transactional // Indispensable pour l'optimisation des requêtes et la sécurité des données
    public ProblemResponseDTO addAlertsToProblem(Long problemId, List<Long> alertIds) {
        log.info("Adding {} alerts to problem {}", alertIds.size(), problemId);

        // 1. Charger le problème UNE SEULE FOIS
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(problemId));

        // 2. Boucler uniquement sur les alertes
        for (Long alertId : alertIds) {
            Alert alert = alertRepository.findById(alertId)
                    .orElseThrow(() -> new AlertNotFoundException(alertId));

            if (alert.getProblem() != null) {
                throw new AlertAlreadyAssignedException(alertId);
            }

            problem.addAlert(alert);
            alert.setProblem(problem);
            alert.setStatus(AlertStatusEnum.IN_PROGRESS);

            alertRepository.save(alert); // Optionnel si @Transactional gère le dirty checking
        }

        // 3. Sauvegarder et mapper UNE SEULE FOIS à la fin
        Problem updatedProblem = problemRepository.save(problem);
        return problemMapper.entityToProblemResponse(updatedProblem);
    }

    // ========== Status Management ==========

    @Override
    public ProblemResponseDTO changeStatus(Long id, ProblemStatusChangeDTO request, Long userId) {
        log.info("Changing status of problem {} to {}", id, request.getNewStatus());

        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));

        // Vérifier les permissions (SUPER_AGENT créateur ou AGENT assigné)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!problem.getUser().getId().equals(userId) && 
            !problem.getAssignedTo().getId().equals(userId)) {
            throw new InvalidProblemException("Vous n'avez pas la permission de changer le statut de ce problème");
        }

        // Sauvegarder l'ancien statut
        ProblemStatusEnum previousStatus = problem.getStatus();

        // Changer le statut du problème
        problem.setStatus(request.getNewStatus());
        if (request.getNewStatus() == ProblemStatusEnum.RESOLVED) {
            problem.setResolvedAt(LocalDateTime.now());
        }

        // Si RESOLVED ou REJECTED: mettre à jour les alertes
        if (request.getNewStatus() == ProblemStatusEnum.RESOLVED) {
            for (Alert alert : problem.getAlerts()) {
                alert.setStatus(AlertStatusEnum.RESOLVED);
                alertRepository.save(alert);
            }
        } else if (request.getNewStatus() == ProblemStatusEnum.REJECTED) {
            for (Alert alert : problem.getAlerts()) {
                alert.setStatus(AlertStatusEnum.REJECTED);
                alertRepository.save(alert);
            }
        }

        Problem updatedProblem = problemRepository.save(problem);

        // Ajouter à l'historique
        ProblemStatusHistory history = ProblemStatusHistory.builder()
                .problem(updatedProblem)
                .previousStatus(previousStatus)
                .newStatus(request.getNewStatus())
                .changedBy(user.getPhone())
                .comment(request.getComment())
                .build();
        historyRepository.save(history);

        // Notifier les citoyens
        notifyAlertCreators(id, request.getNewStatus().toString());

        log.info("Status changed for problem {}", id);
        return problemMapper.entityToProblemResponse(updatedProblem);
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemStatusEnum getProblemStatus(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));

        return problem.getStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProblemStatusHistoryDTO> getStatusHistory(Long problemId, Pageable pageable) {
        log.info("Fetching status history for problem: {}", problemId);

        if (!problemRepository.existsById(problemId)) {
            throw new ProblemNotFoundException(problemId);
        }

        return historyRepository.findByProblemIdOrderByChangedAtDesc(problemId, pageable)
                .map(problemMapper::statusHistoryToDTO);
    }

    // ========== Permission Checks ==========

    @Override
    public void verifySuperAgentRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getRole() != RoleEnum.SUPER_AGENT) {
            throw new NotSuperAgentException();
        }
    }

    @Override
    public void verifyAgentRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getRole() != RoleEnum.AGENT) {
            throw new NotAgentException(userId);
        }
    }

    @Override
    public void verifyCanModifyProblem(Long problemId, Long userId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(problemId));

        if (!problem.getUser().getId().equals(userId) && 
            !problem.getAssignedTo().getId().equals(userId)) {
            throw new InvalidProblemException("Vous n'avez pas la permission de modifier ce problème");
        }
    }

    // ========== Statistics ==========

    @Override
    @Transactional(readOnly = true)
    public Long countProblemsByStatus(ProblemStatusEnum status) {
        return problemRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countProblemsAssignedTo(Long agentId) {
        return problemRepository.countByAssignedToId(agentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countProblemsCreatedBy(Long userId) {
        return problemRepository.countByUserId(userId);
    }

    // ========== Notifications ==========

    @Override
    public void notifyAlertCreators(Long problemId, String newStatus) {
        log.info("Notifying alert creators for problem: {}", problemId);

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ProblemNotFoundException(problemId));

        for (Alert alert : problem.getAlerts()) {
            try {
                AlertStatusEnum statusEnum = AlertStatusEnum.valueOf(newStatus);
                notificationService.notifyAlertStatusChange(
                        alert.getUser(),
                        alert.getTitle(),
                        statusEnum,
                        alert.getId()
                );
            } catch (IllegalArgumentException e) {
                log.warn("Statut inconnu pour la notification: {}", newStatus);
            }
        }
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