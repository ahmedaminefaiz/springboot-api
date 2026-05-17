package org.urban.alert.service;

import org.urban.alert.dto.problemtype.CreateProblemTypeRequest;
import org.urban.alert.dto.problemtype.ProblemTypeResponse;
import org.urban.alert.dto.problemtype.UpdateProblemTypeRequest;

import java.util.List;

/**
 * Service interface for managing Problem Types.
 *
 * <p>Provides business logic operations for creating, reading, updating, and deleting
 * problem types. Follows clean architecture principles by using DTOs for data transfer
 * instead of exposing internal entities directly.
 */
public interface ProblemTypeService {

    /**
     * Retrieves all available problem types.
     *
     * @return a list of {@link ProblemTypeResponse} representing all problem types;
     *         empty list if none exist
     */
    List<ProblemTypeResponse> getAll();

    /**
     * Retrieves a specific problem type by its unique ID.
     *
     * @param id the ID of the problem type to retrieve
     * @return the corresponding {@link ProblemTypeResponse}
     * @throws RuntimeException if no problem type is found with the given ID
     */
    ProblemTypeResponse getById(Long id);

    /**
     * Creates a new problem type based on the provided request data.
     *
     * @param dto the request data containing the new problem type details
     * @return the created {@link ProblemTypeResponse}
     * @throws RuntimeException if a problem type with the same name already exists
     */
    ProblemTypeResponse create(CreateProblemTypeRequest dto);

    /**
     * Updates an existing problem type.
     *
     * @param id  the ID of the problem type to update
     * @param dto the update request containing the modified fields
     * @return the updated {@link ProblemTypeResponse}
     * @throws RuntimeException if no problem type is found or if the new name conflicts with an existing one
     */
    ProblemTypeResponse update(Long id, UpdateProblemTypeRequest dto);

    /**
     * Deletes a specific problem type by its unique ID.
     *
     * @param id the ID of the problem type to delete
     * @throws RuntimeException if no problem type is found with the given ID
     */
    void delete(Long id);
}
