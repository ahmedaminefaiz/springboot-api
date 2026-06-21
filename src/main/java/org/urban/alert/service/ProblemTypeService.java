package org.urban.alert.service;

import org.urban.alert.dto.problemtype.CreateProblemTypeRequestDTO;
import org.urban.alert.dto.problemtype.ProblemTypeResponseDTO;
import org.urban.alert.dto.problemtype.UpdateProblemTypeRequestDTO;

import java.util.List;

/**
 * Service interface for managing Problem Types.
 */
public interface ProblemTypeService {

    /**
     * Retrieves all available problem types.
     */
    List<ProblemTypeResponseDTO> getAll();

    /**
     * Retrieves a specific problem type by its unique ID.
     */
    ProblemTypeResponseDTO getById(Long id);

    /**
     * Creates a new problem type based on the provided request data.
     */
    ProblemTypeResponseDTO create(CreateProblemTypeRequestDTO dto);

    /**
     * Updates an existing problem type.
     */
    ProblemTypeResponseDTO update(Long id, UpdateProblemTypeRequestDTO dto);

    /**
     * Deletes a specific problem type by its unique ID.
     */
    void delete(Long id);
}
