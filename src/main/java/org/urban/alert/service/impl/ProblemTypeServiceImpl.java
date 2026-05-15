package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.problemtype.CreateProblemTypeRequest;
import org.urban.alert.dto.problemtype.ProblemTypeResponse;
import org.urban.alert.dto.problemtype.UpdateProblemTypeRequest;
import org.urban.alert.entity.ProblemType;
import org.urban.alert.entity.User;
import org.urban.alert.repository.ProblemTypeRepository;
import org.urban.alert.repository.UserRepository;
import org.urban.alert.service.ProblemTypeService;
import org.urban.alert.service.mapper.ProblemTypeMapper;

import java.util.List;

/**
 * Implementation of {@link ProblemTypeService}.
 *
 * <p>Handles the business logic for managing problem types. Uses MapStruct for DTO conversions
 * and Spring Data JPA for database interactions. Write operations are transactional.
 */
@Service
@RequiredArgsConstructor
public class ProblemTypeServiceImpl implements ProblemTypeService {

    private final ProblemTypeRepository problemTypeRepository;
    private final ProblemTypeMapper problemTypeMapper;
    private final UserRepository userRepository;

    @Override
    public List<ProblemTypeResponse> getAll() {
        return problemTypeMapper.toResponseList(problemTypeRepository.findAll());
    }

    @Override
    public ProblemTypeResponse getById(Long id) {
        ProblemType problemType = problemTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProblemType not found with id: " + id));
        return problemTypeMapper.toResponse(problemType);
    }

    @Override
    @Transactional
    public ProblemTypeResponse create(CreateProblemTypeRequest dto) {
        if (problemTypeRepository.existsByName(dto.getName())) {
            throw new RuntimeException("ProblemType already exists with name: " + dto.getName());
        }

        ProblemType problemType = problemTypeMapper.toEntity(dto);
        problemType.setAdmin(getCurrentUser());
        
        problemType = problemTypeRepository.save(problemType);
        return problemTypeMapper.toResponse(problemType);
    }

    @Override
    @Transactional
    public ProblemTypeResponse update(Long id, UpdateProblemTypeRequest dto) {
        ProblemType problemType = problemTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProblemType not found with id: " + id));

        if (dto.getName() != null && !dto.getName().equals(problemType.getName())) {
            if (problemTypeRepository.existsByName(dto.getName())) {
                throw new RuntimeException("ProblemType already exists with name: " + dto.getName());
            }
        }

        problemTypeMapper.updateFromDto(dto, problemType);
        
        problemType = problemTypeRepository.save(problemType);
        return problemTypeMapper.toResponse(problemType);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!problemTypeRepository.existsById(id)) {
            throw new RuntimeException("ProblemType not found with id: " + id);
        }
        problemTypeRepository.deleteById(id);
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the current {@link User}
     * @throws RuntimeException if the user is not found
     */
    private User getCurrentUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
