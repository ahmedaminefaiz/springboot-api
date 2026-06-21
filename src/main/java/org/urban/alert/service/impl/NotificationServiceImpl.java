package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.notification.NotificationResponseDTO;
import org.urban.alert.entity.Notification;
import org.urban.alert.entity.User;
import org.urban.alert.entity.enums.AlertStatusEnum;
import org.urban.alert.entity.enums.NotificationTypeEnum;
import org.urban.alert.entity.enums.ProblemStatusEnum;
import org.urban.alert.exception.UserNotFoundException;
import org.urban.alert.repository.NotificationRepository;
import org.urban.alert.service.EmailService;
import org.urban.alert.service.NotificationService;
import org.urban.alert.service.WhatsAppService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    @Override
    public void notifyAlertStatusChange(User user, String alertTitle, AlertStatusEnum newStatus, Long alertId) {
        String message = String.format("Votre alerte '%s' est %s", alertTitle, newStatus.getDisplayName());
        pushNotification(user, message, NotificationTypeEnum.ALERT_STATUS_CHANGE, alertId);
    }

    @Override
    public void notifyAgentProblemAssigned(User agent, String problemTitle, Long problemId) {
        String message = String.format("Un nouveau problème vous a été assigné : '%s'", problemTitle);
        pushNotification(agent, message, NotificationTypeEnum.PROBLEM_ASSIGNED, problemId);
    }

    @Override
    public void notifyAgentProblemStatusChange(User agent, String problemTitle, ProblemStatusEnum newStatus, Long problemId) {
        String message = String.format("Le statut du problème '%s' est maintenant : %s", problemTitle, newStatus.name());
        pushNotification(agent, message, NotificationTypeEnum.PROBLEM_STATUS_CHANGE, problemId);
    }

    private void pushNotification(User user, String message, NotificationTypeEnum type, Long referenceId) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .build();

        NotificationResponseDTO dto = toDTO(notificationRepository.save(notification));

        // Resolve phone before the try-catch to avoid LazyInitializationException inside the catch block
        String phone = user.getPhone();
        try {
            messagingTemplate.convertAndSendToUser(phone, "/queue/notifications", dto);
        } catch (Exception e) {
            log.warn("WebSocket push failed for user {}: {}", phone, e.getMessage());
        }

        log.info("Notification envoyée à {} : {}", phone, message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDTO);
    }

    @Override
    public NotificationResponseDTO markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new UserNotFoundException(notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new SecurityException("Accès refusé à cette notification");
        }

        notification.setIsRead(true);
        return toDTO(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private NotificationResponseDTO toDTO(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType())
                .referenceId(n.getReferenceId())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}