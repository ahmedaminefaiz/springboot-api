package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.usersummary.UserSummaryResponseDTO;
import org.urban.alert.entity.enums.RoleEnum;
import org.urban.alert.entity.User;
import org.urban.alert.entity.enums.UserStatusEnum;
import org.urban.alert.exception.ApprovalException;
import org.urban.alert.exception.UserNotFoundException;
import org.urban.alert.repository.UserRepository;
import org.urban.alert.service.UserManagementService;
import org.urban.alert.service.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserSummaryResponseDTO> getPendingAgents() {
        return userMapper.toSummaryResponseList(
                userRepository.findByRoleAndStatus(RoleEnum.AGENT,
                        UserStatusEnum.PENDING_APPROVAL)
        );
    }

    @Override
    @Transactional
    public void approveAgent(Long agentId) {
        User currentUser = getCurrentUser();
        User agent = findPendingUser(agentId, RoleEnum.AGENT);
        agent.setStatus(UserStatusEnum.ACTIVE);
        agent.setSupervisor(currentUser);
        userRepository.save(agent);
    }

    @Override
    @Transactional
    public void rejectAgent(Long agentId) {
        User agent = findPendingUser(agentId, RoleEnum.AGENT);
        agent.setStatus(UserStatusEnum.REJECTED);
        userRepository.save(agent);
    }

    @Override
    public List<UserSummaryResponseDTO> getPendingSuperAgents() {
        return userMapper.toSummaryResponseList(
                userRepository.findByRoleAndStatus(RoleEnum.SUPER_AGENT,
                        UserStatusEnum.PENDING_APPROVAL)
        );
    }

    @Override
    @Transactional
    public void approveSuperAgent(Long superAgentId) {
        User currentUser = getCurrentUser();
        User superAgent = findPendingUser(superAgentId, RoleEnum.SUPER_AGENT);
        superAgent.setStatus(UserStatusEnum.ACTIVE);
        superAgent.setSupervisor(currentUser);
        userRepository.save(superAgent);
    }

    @Override
    @Transactional
    public void rejectSuperAgent(Long superAgentId) {
        User superAgent = findPendingUser(superAgentId, RoleEnum.SUPER_AGENT);
        superAgent.setStatus(UserStatusEnum.REJECTED);
        userRepository.save(superAgent);
    }

    @Override
    public List<UserSummaryResponseDTO> getActiveAgents() {
        return userMapper.toSummaryResponseList(
                userRepository.findByRoleAndStatus(RoleEnum.AGENT, UserStatusEnum.ACTIVE)
        );
    }

    @Override
    public List<UserSummaryResponseDTO> getMyAgents() {
        User currentUser = getCurrentUser();
        return userMapper.toSummaryResponseList(
                userRepository.findByRoleAndSupervisorId(RoleEnum.AGENT, currentUser.getId())
        );
    }

    private User findPendingUser(Long id, RoleEnum expectedRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (user.getRole() != expectedRole) {
            throw new ApprovalException("User is not a " +
                    expectedRole.name().toLowerCase().replace('_', '-'));
        }
        if (user.getStatus() != UserStatusEnum.PENDING_APPROVAL) {
            throw new ApprovalException("User is not pending approval");
        }
        return user;
    }

    private User getCurrentUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
    }
}