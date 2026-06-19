package org.urban.alert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.urban.alert.dto.notification.NotificationResponseDTO;
import org.urban.alert.entity.User;
import org.urban.alert.entity.enums.AlertStatusEnum;

public interface NotificationService {

    void notifyAlertStatusChange(User user, String alertTitle, AlertStatusEnum newStatus, Long alertId);

    Page<NotificationResponseDTO> getUserNotifications(Long userId, Pageable pageable);

    NotificationResponseDTO markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    Long countUnread(Long userId);
}