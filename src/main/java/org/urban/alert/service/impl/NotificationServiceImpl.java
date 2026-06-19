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

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(NotificationTypeEnum.ALERT_STATUS_CHANGE)
                .referenceId(alertId)
                .build();

        NotificationResponseDTO dto = toDTO(notificationRepository.save(notification));

        // Push WebSocket — sans effet si l'utilisateur est hors ligne
        try {
            messagingTemplate.convertAndSendToUser(user.getPhone(), "/queue/notifications", dto);
        } catch (Exception e) {
            log.warn("WebSocket push failed for user {}: {}", user.getPhone(), e.getMessage());
        }

        // TODO: activer l'email quand la config SMTP est prête
//        try {
//            emailService.send(
//                    user.getEmail(),
//                    "Mise à jour de votre alerte - UrbanAlert",
//                    String.format("Bonjour %s,\n\n%s\n\nL'équipe UrbanAlert", user.getPrenom(), message)
//            );
//        } catch (Exception e) {
//            log.warn("Email failed for user {}: {}", user.getEmail(), e.getMessage());
//        }

        // TODO: activer WhatsApp quand les credentials sont configurés
//        try {
//            whatsAppService.sendNotification(user.getPhone(), message);
//        } catch (Exception e) {
//            log.warn("WhatsApp failed for user {}: {}", user.getPhone(), e.getMessage());
//        }

        log.info("Notification envoyée à {} : {}", user.getPhone(), message);
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