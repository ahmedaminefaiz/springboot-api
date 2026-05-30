package org.urban.alert.service;

import java.util.List;
import org.urban.alert.dto.UserSummaryResponseDTO;

public interface UserManagementService {

    List<UserSummaryResponseDTO> getPendingAgents();

    void approveAgent(Long agentId);

    void rejectAgent(Long agentId);

    List<UserSummaryResponseDTO> getPendingSuperAgents();

    void approveSuperAgent(Long superAgentId);

    void rejectSuperAgent(Long superAgentId);
}