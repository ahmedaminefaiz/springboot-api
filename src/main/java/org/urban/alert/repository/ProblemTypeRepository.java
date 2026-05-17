package org.urban.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.ProblemType;

import java.util.Optional;

/**
 * JPA repository for {@link ProblemType} entities.
 *
 * <p>Extends {@link JpaRepository} with business-specific query methods for
 * name-based lookups and existence checks.
 */
@Repository
public interface ProblemTypeRepository extends JpaRepository<ProblemType, Long> {

    /**
     * Checks whether a problem type with the given name already exists.
     *
     * @param name the name to check
     * @return {@code true} if a problem type with this name exists, {@code false} otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds a problem type by name.
     *
     * @param name the name to search for
     * @return an {@link Optional} containing the matching problem type, or empty if none found
     */
    Optional<ProblemType> findByName(String name);
}
