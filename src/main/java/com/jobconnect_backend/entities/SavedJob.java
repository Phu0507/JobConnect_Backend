package com.jobconnect_backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SavedJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer savedJobId;

    private LocalDate savedAt;

    @ManyToOne
    @JoinColumn(name = "job_seeker_profile_id")
    private JobSeekerProfile jobSeekerProfile;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
}