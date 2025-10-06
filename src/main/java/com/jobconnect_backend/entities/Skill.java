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
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer skillId;

    private String name;
}