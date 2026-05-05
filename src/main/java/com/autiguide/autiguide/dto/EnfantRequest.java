package com.autiguide.autiguide.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EnfantRequest {

    private String nom;
    private LocalDate dateNaissance;
    private String sexe;
    private Long parentId;
}