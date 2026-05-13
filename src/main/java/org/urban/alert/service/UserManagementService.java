package org.urban.alert.service;

import java.util.List;

import org.urban.alert.dto.UserSummaryResponse;

/**
 * Contract for administrative management of Agent and Super-Agent accounts.
 *
 * <p>Provides operations to list users awaiting approval and to approve or reject them,
 * driving status transitions within the Urban Alert onboarding workflow.
 */
public interface UserManagementService {

    /**
     * Returns all Agent accounts currently awaiting admin approval.
     *
     * @return a list of {@link UserSummaryResponse} representing pending agents;
     *         empty list if none are pending
     */
    List<UserSummaryResponse> getPendingAgents();

    /**
     * Approves an Agent account, activating it and assigning the authenticated admin as supervisor.
     *
     * @param agentId the ID of the agent to approve
     * @throws org.urban.alert.exception.UserNotFoundException if no user exists with the given ID
     * @throws org.urban.alert.exception.ApprovalException if the user is not an agent or is not in pending approval status
     */
    void approveAgent(Long agentId);

    /**
     * Rejects an Agent account, permanently marking it as rejected.
     *
     * @param agentId the ID of the agent to reject
     * @throws org.urban.alert.exception.UserNotFoundException if no user exists with the given ID
     * @throws org.urban.alert.exception.ApprovalException if the user is not an agent or is not in pending approval status
     */
    void rejectAgent(Long agentId);

    /**
     * Returns all Super-Agent accounts currently awaiting admin approval.
     *
     * @return a list of {@link UserSummaryResponse} representing pending super-agents;
     *         empty list if none are pending
     */
    List<UserSummaryResponse> getPendingSuperAgents();

    /**
     * Approves a Super-Agent account, activating it and assigning the authenticated admin as supervisor.
     *
     * @param superAgentId the ID of the super-agent to approve
     * @throws org.urban.alert.exception.UserNotFoundException if no user exists with the given ID
     * @throws org.urban.alert.exception.ApprovalException if the user is not a super-agent or is not in pending approval status
     */
    void approveSuperAgent(Long superAgentId);

    /**
     * Rejects a Super-Agent account, permanently marking it as rejected.
     *
     * @param superAgentId the ID of the super-agent to reject
     * @throws org.urban.alert.exception.UserNotFoundException if no user exists with the given ID
     * @throws org.urban.alert.exception.ApprovalException if the user is not a super-agent or is not in pending approval status
     */
    void rejectSuperAgent(Long superAgentId);
}