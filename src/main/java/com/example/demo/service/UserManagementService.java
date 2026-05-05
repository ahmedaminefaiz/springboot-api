package com.example.demo.service;

import com.example.demo.dto.UserSummaryResponse;
import java.util.List;

public interface UserManagementService {
    List<UserSummaryResponse> getPendingAgents();
    void approveAgent(Long agentId);
    void rejectAgent(Long agentId);
    List<UserSummaryResponse> getPendingSuperAgents();
    void approveSuperAgent(Long superAgentId);
    void rejectSuperAgent(Long superAgentId);
}