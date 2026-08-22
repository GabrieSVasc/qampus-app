package com.project.qampus.service;

import com.project.qampus.dto.matriculaturma.MatriculaTurmaRequestDTO;
import com.project.qampus.dto.matriculaturma.MatriculaTurmaResponseDTO;
import com.project.qampus.model.MatriculaTurma;
import com.project.qampus.model.MatriculaTurmaId;
import com.project.qampus.model.Turma;
import com.project.qampus.model.User;
import com.project.qampus.repositories.MatriculaTurmaRepository;
import com.project.qampus.repositories.TurmaRepository;
import com.project.qampus.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MatriculaTurmaService {

    private final MatriculaTurmaRepository matriculaTurmaRepository;
    private final TurmaRepository turmaRepository;
    private final UserRepository userRepository;

    public MatriculaTurmaService(MatriculaTurmaRepository matriculaTurmaRepository, TurmaRepository turmaRepository, UserRepository userRepository) {
        this.matriculaTurmaRepository = matriculaTurmaRepository;
        this.turmaRepository = turmaRepository;
        this.userRepository = userRepository;
    }

    public List<MatriculaTurmaResponseDTO> listarTodos() {
        return matriculaTurmaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public MatriculaTurmaResponseDTO buscarPorId(Long idTurma, Long idAluno) {
        MatriculaTurmaId id = new MatriculaTurmaId(idTurma, idAluno);
        MatriculaTurma matricula = matriculaTurmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula não encontrada"));
        return toResponseDTO(matricula);
    }

    public MatriculaTurmaResponseDTO criar(MatriculaTurmaRequestDTO dto) {
        MatriculaTurma matricula = new MatriculaTurma();
        matricula.setTurma(buscarTurma(dto.idTurma()));
        matricula.setAluno(buscarAluno(dto.idAluno()));
        return toResponseDTO(matriculaTurmaRepository.save(matricula));
    }

    public void deletar(Long idTurma, Long idAluno) {
        MatriculaTurmaId id = new MatriculaTurmaId(idTurma, idAluno);
        if (!matriculaTurmaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula não encontrada");
        }
        matriculaTurmaRepository.deleteById(id);
    }

    private Turma buscarTurma(Long idTurma) {
        return turmaRepository.findById(idTurma)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Turma não encontrada"));
    }

    private User buscarAluno(String idAluno) {
        return userRepository.findById(idAluno)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aluno não encontrado"));
    }

    private MatriculaTurmaResponseDTO toResponseDTO(MatriculaTurma matricula) {
        return new MatriculaTurmaResponseDTO(
                matricula.getTurma().getId(),
                matricula.getAluno().getId()
        );
    }
}