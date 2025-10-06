package com.jobconnect_backend.dto.dto;

import com.jobfind.dto.response.IndustryReponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class CompanyDTO {
    private Integer companyId;
    private String companyName;
    private String logoPath;
    private List<IndustryReponse> industry;
    private String website;
    private String description;
    private String email;
    private String phoneNumber;
    private boolean isVip;
    private Integer vipLevel;
    private LocalDateTime vipExpiryDate;
    private Integer createJobCount;
}
