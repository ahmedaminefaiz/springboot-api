package com.example.demo.service.impl;

import com.example.demo.dto.UserSummaryResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserStatus;
import com.example.demo.exception.ApprovalException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserManagementService;
import com.example.demo.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserSummaryResponse> getPendingAgents() {
        return userMapper.toSummaryResponseList(
                userRepository.findByRoleAndStatus(Role.AGENT,
                        UserStatus.PENDING_APPROVAL)
        );
    }

    @Override
    @Transactional
    public void approveAgent(Long agentId) {
        User currentUser = getCurrentUser();
        User agent = findPendingUser(agentId, Role.AGENT);
        agent.setStatus(UserStatus.ACTIVE);
        agent.setSupervisor(currentUser);
        userRepository.save(agent);
    }

    @Override
    @Transactional
    public void rejectAgent(Long agentId) {
        User agent = findPendingUser(agentId, Role.AGENT);
        agent.setStatus(UserStatus.REJECTED);
        userRepository.save(agent);
    }

    @Override
    public List<UserSummaryResponse> getPendingSuperAgents() {
        return userMapper.toSummaryResponseList(
                userRepository.findByRoleAndStatus(Role.SUPER_AGENT,
                        UserStatus.PENDING_APPROVAL)
        );
    }

    @Override
    @Transactional
    public void approveSuperAgent(Long superAgentId) {
        User currentUser = getCurrentUser();
        User superAgent = findPendingUser(superAgentId, Role.SUPER_AGENT);
        superAgent.setStatus(UserStatus.ACTIVE);
        superAgent.setSupervisor(currentUser);
        userRepository.save(superAgent);
    }

    @Override
    @Transactional
    public void rejectSuperAgent(Long superAgentId) {
        User superAgent = findPendingUser(superAgentId, Role.SUPER_AGENT);
        superAgent.setStatus(UserStatus.REJECTED);
        userRepository.save(superAgent);
    }

    private User findPendingUser(Long id, Role expectedRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (user.getRole() != expectedRole) {
            throw new ApprovalException("User is not a " +
                    expectedRole.name().toLowerCase().replace('_', '-'));
        }
        if (user.getStatus() != UserStatus.PENDING_APPROVAL) {
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