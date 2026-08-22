package com.project.qampus.repositories;

import com.project.qampus.model.MatriculaTurma;
import com.project.qampus.model.MatriculaTurmaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaTurmaRepository extends JpaRepository<MatriculaTurma, MatriculaTurmaId> {
}