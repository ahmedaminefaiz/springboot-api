package org.urban.alert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.urban.alert.dto.notification.NotificationResponseDTO;
import org.urban.alert.entity.User;
import org.urban.alert.entity.enums.AlertStatusEnum;
import org.urban.alert.entity.enums.ProblemStatusEnum;

public interface NotificationService {

    void notifyAlertReceived(User user, String alertTitle, Long alertId);

    void notifyAlertStatusChange(User user, String alertTitle, AlertStatusEnum newStatus, Long alertId);

    void notifyAgentProblemAssigned(User agent, String problemTitle, Long problemId);

    void notifyAgentProblemStatusChange(User agent, String problemTitle, ProblemStatusEnum newStatus, Long problemId);

    Page<NotificationResponseDTO> getUserNotifications(Long userId, Pageable pageable);

    NotificationResponseDTO markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    Long countUnread(Long userId);

    void notifyAgentInterventionAssigned(User agent, String problemTitle, Long interventionId);
}