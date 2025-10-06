package com.jobconnect_backend.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryResponse {
    private Integer jobCategoryId;
    private String name;
}
