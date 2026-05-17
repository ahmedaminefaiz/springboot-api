package org.urban.alert.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.urban.alert.dto.problemtype.CreateProblemTypeRequest;
import org.urban.alert.dto.problemtype.ProblemTypeResponse;
import org.urban.alert.dto.problemtype.UpdateProblemTypeRequest;
import org.urban.alert.entity.ProblemType;

import java.util.List;

/**
 * MapStruct mapper for {@link ProblemType} entity to DTO conversions.
 *
 * <p>Used in the problem type management layer to project entity data into lightweight
 * {@link ProblemTypeResponse} objects for API consumers, and update entities from requests.
 */
@Mapper(componentModel = "spring")
public interface ProblemTypeMapper {

    /**
     * Converts a {@link CreateProblemTypeRequest} to a {@link ProblemType} entity.
     *
     * @param dto the request DTO
     * @return a new {@link ProblemType} entity
     */
    ProblemType toEntity(CreateProblemTypeRequest dto);

    /**
     * Updates an existing {@link ProblemType} entity from an {@link UpdateProblemTypeRequest}.
     * Null values in the DTO are ignored.
     *
     * @param dto    the update request DTO
     * @param entity the entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateProblemTypeRequest dto, @MappingTarget ProblemType entity);

    /**
     * Converts a {@link ProblemType} entity to a {@link ProblemTypeResponse}.
     *
     * @param entity the problem type entity to convert
     * @return a {@link ProblemTypeResponse} containing the problem type's data
     */
    @Mapping(target = "adminName", expression = "java(entity.getAdmin() != null ? entity.getAdmin().getNom() + \" \" + entity.getAdmin().getPrenom() : null)")
    ProblemTypeResponse toResponse(ProblemType entity);

    /**
     * Converts a list of {@link ProblemType} entities to a list of {@link ProblemTypeResponse}.
     *
     * @param entities the list of problem type entities to convert
     * @return a list of {@link ProblemTypeResponse}; empty list if input is empty
     */
    List<ProblemTypeResponse> toResponseList(List<ProblemType> entities);
}
