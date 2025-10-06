package com.jobconnect_backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Integer companyId;

    private String companyName;
    private String logoPath;
    @ManyToMany
    @JoinTable(
            name = "Company_Industry",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "industry_id")
    )
    private List<Industry> industry;
    private String website;
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(columnDefinition = "boolean default false")
    private Boolean isVerified;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer createJobCount;
}