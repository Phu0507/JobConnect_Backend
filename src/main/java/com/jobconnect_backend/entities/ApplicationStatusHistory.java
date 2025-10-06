package com.jobconnect_backend.entities;
import com.jobfind.models.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemStatusId;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    private ApplicationStatus applicationStatus;

    private LocalDateTime time;
}
