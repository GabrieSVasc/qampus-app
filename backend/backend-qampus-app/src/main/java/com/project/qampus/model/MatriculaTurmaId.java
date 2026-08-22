package com.project.qampus.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MatriculaTurmaId implements Serializable {
    private Long idTurma;
    private Long idAluno;
}