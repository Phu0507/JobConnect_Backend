package com.jobconnect_backend.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndustryReponse {
    private Integer industryId;
    private String name;
}
