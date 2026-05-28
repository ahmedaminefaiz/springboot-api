package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.problemtype.CreateProblemTypeRequestDTO;
import org.urban.alert.dto.problemtype.ProblemTypeResponseDTO;
import org.urban.alert.dto.problemtype.UpdateProblemTypeRequestDTO;
import org.urban.alert.entity.ProblemType;
import org.urban.alert.entity.User;
import org.urban.alert.repository.ProblemTypeRepository;
import org.urban.alert.repository.UserRepository;
import org.urban.alert.service.ProblemTypeService;
import org.urban.alert.service.mapper.ProblemTypeMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemTypeServiceImpl implements ProblemTypeService {

    private final ProblemTypeRepository problemTypeRepository;
    private final ProblemTypeMapper problemTypeMapper;
    private final UserRepository userRepository;

    @Override
    public List<ProblemTypeResponseDTO> getAll() {
        return problemTypeMapper.toResponseList(problemTypeRepository.findAll());
    }

    @Override
    public ProblemTypeResponseDTO getById(Long id) {
        ProblemType problemType = problemTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProblemType not found with id: " + id));
        return problemTypeMapper.toResponse(problemType);
    }

    @Override
    @Transactional
    public ProblemTypeResponseDTO create(CreateProblemTypeRequestDTO dto) {
        if (problemTypeRepository.existsByName(dto.getName())) {
            throw new RuntimeException("ProblemType already exists with name: " + dto.getName());
        }

        ProblemType problemType = problemTypeMapper.toEntity(dto);
        problemType.setAdmin(getCurrentUser());
        
        return problemTypeMapper.toResponse(problemTypeRepository.save(problemType));
    }

    @Override
    @Transactional
    public ProblemTypeResponseDTO update(Long id, UpdateProblemTypeRequestDTO dto) {
        ProblemType problemType = problemTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProblemType not found with id: " + id));

        if (dto.getName() != null && !dto.getName().equals(problemType.getName())) {
            if (problemTypeRepository.existsByName(dto.getName())) {
                throw new RuntimeException("ProblemType already exists with name: " + dto.getName());
            }
        }

        problemTypeMapper.updateFromDto(dto, problemType);
        
        return problemTypeMapper.toResponse(problemTypeRepository.save(problemType));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!problemTypeRepository.existsById(id)) {
            throw new RuntimeException("ProblemType not found with id: " + id);
        }
        problemTypeRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
