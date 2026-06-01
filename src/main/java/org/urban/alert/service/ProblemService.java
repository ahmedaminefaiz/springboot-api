package org.urban.alert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.problem.*;
import org.urban.alert.entity.enums.ProblemStatusEnum;

public interface ProblemService {

    // ========== CRUD Operations ==========

    /**
     * Crée un problème (Logique métier complexe):
     * 1. Vérifie que l'utilisateur qui crée est un SUPER_AGENT
     * 2. Vérifie que assigned_to est un AGENT
     * 3. Crée le problème avec les alertes spécifiées
     * 4. Change le statut des alertes en IN_PROGRESS
     * 5. Ajoute à l'historique des statuts
     */
    ProblemResponseDTO createProblem(ProblemCreateDTO request, Long createdByUserId);

    /**
     * Récupère un problème par ID
     */
    ProblemResponseDTO getProblemById(Long id);

    /**
     * Récupère tous les problèmes avec pagination
     */
    Page<ProblemResponseDTO> getAllProblems(Pageable pageable);

    /**
     * Récupère les problèmes créés par un SuperAgent
     */
    Page<ProblemResponseDTO> getProblemsCreatedBy(Long userId, Pageable pageable);

    /**
     * Récupère les problèmes assignés à un Agent
     */
    Page<ProblemResponseDTO> getProblemsAssignedTo(Long agentId, Pageable pageable);

    /**
     * Récupère les problèmes par statut
     */
    Page<ProblemResponseDTO> getProblemsByStatus(ProblemStatusEnum status, Pageable pageable);

    /**
     * Met à jour un problème (ajouter/retirer des alertes, changer l'agent assigné)
     * Vérifie que l'utilisateur est un SUPER_AGENT ou AGENT assigné
     */
    ProblemResponseDTO updateProblem(Long id, ProblemUpdateDTO request, Long userId);

    /**
     * Supprime un problème (seulement si le problème n'est pas RESOLVED ou REJECTED)
     * Vérifie que l'utilisateur est le SUPER_AGENT qui a créé
     */
    void deleteProblem(Long id, Long userId);

    // ========== Alert Management ==========

    /**
     * Ajoute une alerte au problème
     * 1. Vérifie que l'alerte n'est pas déjà assignée à un problème
     * 2. Assigne l'alerte au problème
     * 3. Change le statut de l'alerte en IN_PROGRESS
     */
    ProblemResponseDTO addAlertToProblem(Long problemId, Long alertId);

    /**
     * Retire une alerte du problème
     * 1. Retire l'alerte du problème
     * 2. Change le statut de l'alerte en NEW
     */
    ProblemResponseDTO removeAlertFromProblem(Long problemId, Long alertId);

    /**
     * Ajoute plusieurs alertes au problème
     */
    ProblemResponseDTO addAlertsToProblem(Long problemId, java.util.List<Long> alertIds);

    // ========== Status Management ==========

    /**
     * Change le statut d'un problème
     * 1. Vérifie les permissions (SUPER_AGENT ou AGENT assigné)
     * 2. Change le statut du problème
     * 3. Si RESOLVED: toutes les alertes → RESOLVED
     * 4. Si REJECTED: toutes les alertes → REJECTED
     * 5. Ajoute un enregistrement à l'historique des statuts
     * 6. Notifie les citoyens qui ont créé les alertes
     */
    ProblemResponseDTO changeStatus(Long id, ProblemStatusChangeDTO request, Long userId);

    /**
     * Obtient le statut actuel du problème
     */
    ProblemStatusEnum getProblemStatus(Long id);

    /**
     * Récupère l'historique complet des changements de statut
     */
    Page<ProblemStatusHistoryDTO> getStatusHistory(Long problemId, Pageable pageable);

    // ========== Permission Checks ==========

    /**
     * Vérifie que l'utilisateur est un SUPER_AGENT
     */
    void verifySuperAgentRole(Long userId);

    /**
     * Vérifie que l'utilisateur est un AGENT
     */
    void verifyAgentRole(Long userId);

    /**
     * Vérifie que l'utilisateur peut modifier ce problème
     * (créateur SUPER_AGENT ou AGENT assigné)
     */
    void verifyCanModifyProblem(Long problemId, Long userId);

    // ========== Statistics ==========

    /**
     * Compte les problèmes par statut
     */
    Long countProblemsByStatus(ProblemStatusEnum status);

    /**
     * Compte les problèmes assignés à un agent
     */
    Long countProblemsAssignedTo(Long agentId);

    /**
     * Compte les problèmes créés par un utilisateur
     */
    Long countProblemsCreatedBy(Long userId);

    // ========== Notifications ==========

    /**
     * Notifie les citoyens qui ont créé les alertes du changement de statut
     * Message: "Votre alerte est {statusAlert}"
     */
    void notifyAlertCreators(Long problemId, String newStatus);

    @Transactional(readOnly = true)
    Long getCurrentUserId();
}