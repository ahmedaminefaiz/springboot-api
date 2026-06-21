package org.urban.alert.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.entity.enums.NotificationTypeEnum;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private Long id;
    private String message;
    private NotificationTypeEnum type;
    private Long referenceId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}