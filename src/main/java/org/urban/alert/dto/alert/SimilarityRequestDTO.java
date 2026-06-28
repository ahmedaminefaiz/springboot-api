package org.urban.alert.dto.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimilarityRequestDTO {

    private String sourceImageUrl;
    private List<SimilarityCandidateDTO> candidates;
}
