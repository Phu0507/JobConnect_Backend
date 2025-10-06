package com.jobconnect_backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JobPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobPositionId;

    private String name;
}
