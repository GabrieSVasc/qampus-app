package com.project.qampus.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Entity
@Table(name = "matricula_turma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaTurma {

    @EmbeddedId
    private MatriculaTurmaId id = new MatriculaTurmaId();

    @ManyToOne
    @MapsId("idTurma")
    @JoinColumn(name = "idTurma", nullable = false)
    private Turma turma;

    @ManyToOne
    @MapsId("idAluno")
    @JoinColumn(name = "idAluno", nullable = false)
    private User aluno;
}