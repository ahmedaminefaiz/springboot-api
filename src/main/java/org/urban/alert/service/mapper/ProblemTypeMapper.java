package org.urban.alert.service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.urban.alert.dto.problemtype.CreateProblemTypeRequestDTO;
import org.urban.alert.dto.problemtype.ProblemTypeResponseDTO;
import org.urban.alert.dto.problemtype.UpdateProblemTypeRequestDTO;
import org.urban.alert.entity.ProblemType;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProblemTypeMapper {

    ProblemType toEntity(CreateProblemTypeRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateProblemTypeRequestDTO dto, @MappingTarget ProblemType entity);

    @Mapping(target = "adminName", expression = "java(entity.getAdmin() != null ? entity.getAdmin().getNom() + \" \" + entity.getAdmin().getPrenom() : null)")
    ProblemTypeResponseDTO toResponse(ProblemType entity);

    List<ProblemTypeResponseDTO> toResponseList(List<ProblemType> entities);
}
