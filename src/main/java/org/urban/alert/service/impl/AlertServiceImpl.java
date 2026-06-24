package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.alert.AddMediaRequestDTO;
import org.urban.alert.dto.alert.CreateAlertRequestDTO;
import org.urban.alert.dto.alert.AlertResponseDTO;
import org.urban.alert.dto.alert.UpdateAlertRequestDTO;
import org.urban.alert.entity.Alert;
import org.urban.alert.entity.enums.AlertStatusEnum;
import org.urban.alert.entity.User;
import org.urban.alert.exception.*;
import org.urban.alert.exception.alert.InvalidAlertException;
import org.urban.alert.exception.alert.AlertNotFoundException;
import org.urban.alert.repository.ProblemTypeRepository;
import org.urban.alert.repository.AlertRepository;
import org.urban.alert.service.CloudinaryService;
import org.urban.alert.repository.UserRepository;
import org.urban.alert.service.AlertService;
import org.urban.alert.service.NotificationService;
import org.urban.alert.service.mapper.AlertMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final ProblemTypeRepository problemTypeRepository;
    private final AlertMapper alertMapper;
    private final NotificationService notificationService;
    private final CloudinaryService cloudinaryService;


    // ========== CRUD Operations ==========

    @Override
    public AlertResponseDTO createAlert(CreateAlertRequestDTO request, Long userId) {
        log.info("Creating new alert for user: {}", userId);

        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Vérifier que la catégorie existe
        var category = problemTypeRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ProblemTypeNotFoundException(request.getCategoryId()));

        // Créer l'alerte avec MapStruct
        Alert alert = alertMapper.createAlertRequestToEntity(request);
        alert.setUser(user);
        alert.setCategory(category);

        Alert savedAlert = alertRepository.save(alert);
        log.info("Alert created with ID: {}", savedAlert.getId());

        notificationService.notifyAlertReceived(user, savedAlert.getTitle(), savedAlert.getId());

        return alertMapper.entityToAlertResponse(savedAlert);
    }

    @Override
    @Transactional(readOnly = true)
    public AlertResponseDTO getAlertById(Long id) {
        log.info("Fetching alert with ID: {}", id);

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new AlertNotFoundException(id));

        return alertMapper.entityToAlertResponse(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponseDTO> getAllAlerts(Pageable pageable) {
        log.info("Fetching all alerts with pagination");

        return alertRepository.findAll(pageable)
                .map(alertMapper::entityToAlertResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponseDTO> getUnqualifiedAlerts(Pageable pageable) {
        log.info("Fetching unqualified alerts (problem IS NULL)");
        return alertRepository.findByProblemIsNull(pageable)
                .map(alertMapper::entityToAlertResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponseDTO> getUserAlerts(Long userId, Pageable pageable) {
        log.info("Fetching alerts for user: {}", userId);

        // Vérifier que l'utilisateur existe
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return alertRepository.findByUserId(userId, pageable)
                .map(alertMapper::entityToAlertResponse);
    }

    @Override
    public AlertResponseDTO updateAlert(Long id, UpdateAlertRequestDTO request, Long userId) {
        log.info("Updating alert with ID: {} for user: {}", id, userId);

        Alert alert = alertRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AlertNotFoundException(id));

        // Vérifier que le statut est NEW avant modification
        verifyAlertIsNew(id);

        // Mettre à jour la catégorie si elle est fournie
        if (request.getCategoryId() != null && !request.getCategoryId().equals(alert.getCategory().getId())) {
            var newCategory = problemTypeRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ProblemTypeNotFoundException(request.getCategoryId()));
            alert.setCategory(newCategory);
        }

        // Mettre à jour les champs avec MapStruct
        alertMapper.updateAlertFromRequest(request, alert);

        Alert updatedAlert = alertRepository.save(alert);
        log.info("Alert updated with ID: {}", updatedAlert.getId());

        return alertMapper.entityToAlertResponse(updatedAlert);
    }

    @Override
    public void deleteAlert(Long id, Long userId) {
        log.info("Deleting alert with ID: {} for user: {}", id, userId);

        Alert alert = alertRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AlertNotFoundException(id));

        // Vérifier que le statut est NEW avant suppression
        verifyAlertIsNew(id);

        alertRepository.deleteById(id);
        log.info("Alert deleted with ID: {}", id);
    }

    // ========== Status Verification ==========

    @Override
    public void verifyAlertIsNew(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));

        if (!AlertStatusEnum.NEW.equals(alert.getStatus())) {
            throw new InvalidAlertException(
                    String.format("L'alerte avec l'ID %d ne peut pas être modifiée. Son statut est: %s",
                            alertId, alert.getStatus())
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AlertStatusEnum getAlertStatus(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));

        return alert.getStatus();
    }

    @Override
    public AlertResponseDTO changeStatus(Long alertId, AlertStatusEnum newStatus) {
        log.info("Changing status of alert {} to {}", alertId, newStatus);

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));

        alert.setStatus(newStatus);
        Alert updatedAlert = alertRepository.save(alert);

        log.info("Status changed for alert {}", alertId);
        return alertMapper.entityToAlertResponse(updatedAlert);
    }

    // ========== Media Operations ==========

    @Override
    public AlertResponseDTO addImage(Long alertId, AddMediaRequestDTO request) {
        log.info("Adding image to alert: {}", alertId);
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        alert.getImages().add(request.getMediaUrl());
        return alertMapper.entityToAlertResponse(alertRepository.save(alert));
    }

    @Override
    public AlertResponseDTO addVideo(Long alertId, AddMediaRequestDTO request) {
        log.info("Adding video to alert: {}", alertId);
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        alert.getVideos().add(request.getMediaUrl());
        return alertMapper.entityToAlertResponse(alertRepository.save(alert));
    }

    @Override
    public AlertResponseDTO removeImage(Long alertId, String imageUrl) {
        log.info("Removing image from alert: {}", alertId);
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        if (!alert.getImages().remove(imageUrl)) {
            throw new InvalidAlertException("L'image n'a pas été trouvée dans l'alerte");
        }
        AlertResponseDTO response = alertMapper.entityToAlertResponse(alertRepository.save(alert));
        cloudinaryService.deleteResource(imageUrl, "image");
        return response;
    }

    @Override
    public AlertResponseDTO removeVideo(Long alertId, String videoUrl) {
        log.info("Removing video from alert: {}", alertId);
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        if (!alert.getVideos().remove(videoUrl)) {
            throw new InvalidAlertException("La vidéo n'a pas été trouvée dans l'alerte");
        }
        AlertResponseDTO response = alertMapper.entityToAlertResponse(alertRepository.save(alert));
        cloudinaryService.deleteResource(videoUrl, "video");
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponseDTO> getSimilarAlerts(Long alertId, double radiusMeters) {
        log.info("Finding similar alerts for alert {} within {}m", alertId, radiusMeters);
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        return alertRepository.findSimilarAlerts(
                alertId,
                alert.getCategory().getId(),
                alert.getLatitude().doubleValue(),
                alert.getLongitude().doubleValue(),
                radiusMeters
        ).stream().map(alertMapper::entityToAlertResponse).collect(Collectors.toList());
    }

    // ========== Search & Filter ==========

    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponseDTO> getAlertsByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching alerts by category: {}", categoryId);

        return alertRepository.findByCategoryId(categoryId, pageable)
                .map(alertMapper::entityToAlertResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponseDTO> getAlertsByStatus(AlertStatusEnum status, Pageable pageable) {
        log.info("Fetching alerts by status: {}", status);

        return alertRepository.findByStatus(status, pageable)
                .map(alertMapper::entityToAlertResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponseDTO> searchAlerts(String keyword, Pageable pageable) {
        log.info("Searching alerts with keyword: {}", keyword);

        return alertRepository.searchByKeyword(keyword, pageable)
                .map(alertMapper::entityToAlertResponse);
    }

    // ========== Statistics ==========

    @Override
    @Transactional(readOnly = true)
    public Long countAlertsByStatus(AlertStatusEnum status) {
        return alertRepository.countByStatus(status);
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

    @Override
    @Transactional(readOnly = true)
    public Long countUserAlerts(Long userId) {
        return alertRepository.countByUserId(userId);
    }
}
