package org.urban.alert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.urban.alert.dto.alert.AddMediaRequestDTO;
import org.urban.alert.dto.alert.CreateAlertRequestDTO;
import org.urban.alert.dto.alert.AlertResponseDTO;
import org.urban.alert.dto.alert.UpdateAlertRequestDTO;
import org.urban.alert.entity.enums.AlertStatusEnum;

public interface AlertService {

    /**
     * Obtient l'ID de l'utilisateur connecté depuis le contexte de sécurité.
     */
    Long getCurrentUserId();

    // ========== CRUD Operations ==========

    /**
     * Crée une nouvelle alerte pour l'utilisateur actuel
     */
    AlertResponseDTO createAlert(CreateAlertRequestDTO request, Long userId);

    /**
     * Récupère une alerte par son ID
     */
    AlertResponseDTO getAlertById(Long id);

    /**
     * Récupère toutes les alertes avec pagination
     */
    Page<AlertResponseDTO> getAllAlerts(Pageable pageable);

    /**
     * Récupère les alertes de l'utilisateur actuel
     */
    Page<AlertResponseDTO> getUserAlerts(Long userId, Pageable pageable);

    /**
     * Met à jour une alerte (seulement si le statut est NEW)
     */
    AlertResponseDTO updateAlert(Long id, UpdateAlertRequestDTO request, Long userId);

    /**
     * Supprime une alerte (seulement si le statut est NEW)
     */
    void deleteAlert(Long id, Long userId);

    // ========== Status Verification ==========

    /**
     * Vérifie que le statut de l'alerte est NEW
     */
    void verifyAlertIsNew(Long alertId);

    /**
     * Obtient le statut de l'alerte
     */
    AlertStatusEnum getAlertStatus(Long alertId);

    /**
     * Vérifie si l'utilisateur a l'autorisation de modifier le statut d'une alerte.
     */
    void verifyUserCanChangeStatus(Long userId);

    /**
     * Change le statut d'une alerte
     */
    AlertResponseDTO changeStatus(Long alertId, AlertStatusEnum newStatus);

    // ========== Media Operations ==========

    /**
     * Ajoute une photo à l'alerte
     */
    AlertResponseDTO addImage(Long alertId, AddMediaRequestDTO request);

    /**
     * Ajoute une vidéo à l'alerte
     */
    AlertResponseDTO addVideo(Long alertId, AddMediaRequestDTO request);

    /**
     * Supprime une photo de l'alerte
     */
    AlertResponseDTO removeImage(Long alertId, String imageUrl);

    /**
     * Supprime une vidéo de l'alerte
     */
    AlertResponseDTO removeVideo(Long alertId, String videoUrl);

    // ========== Search & Filter ==========

    /**
     * Recherche les alertes par catégorie
     */
    Page<AlertResponseDTO> getAlertsByCategory(Long categoryId, Pageable pageable);

    /**
     * Recherche les alertes par statut
     */
    Page<AlertResponseDTO> getAlertsByStatus(AlertStatusEnum status, Pageable pageable);

    /**
     * Recherche les alertes par mot-clé
     */
    Page<AlertResponseDTO> searchAlerts(String keyword, Pageable pageable);

    // ========== Statistics ==========

    /**
     * Obtient le nombre d'alertes par statut
     */
    Long countAlertsByStatus(AlertStatusEnum status);

    /**
     * Obtient le nombre d'alertes de l'utilisateur
     */
    Long countUserAlerts(Long userId);
}
