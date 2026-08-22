package com.project.qampus.service;

import com.project.qampus.dto.turma.TurmaRequestDTO;
import com.project.qampus.dto.turma.TurmaResponseDTO;
import com.project.qampus.model.Disciplina;
import com.project.qampus.model.Turma;
import com.project.qampus.model.User;
import com.project.qampus.repositories.DisciplinaRepository;
import com.project.qampus.repositories.TurmaRepository;
import com.project.qampus.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final UserRepository userRepository;

    public TurmaService(TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository, UserRepository userRepository) {
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.userRepository = userRepository;
    }

    public List<TurmaResponseDTO> listarTodos() {
        return turmaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public TurmaResponseDTO buscarPorId(Long id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada"));
        return toResponseDTO(turma);
    }

    public TurmaResponseDTO criar(TurmaRequestDTO dto) {
        Turma turma = new Turma();
        turma.setPeriodo(dto.periodo());
        turma.setDisciplina(buscarDisciplina(dto.codigoDisciplina()));
        turma.setProfessor(buscarProfessor(dto.emailProfessor()));
        return toResponseDTO(turmaRepository.save(turma));
    }

    public TurmaResponseDTO atualizar(Long id, TurmaRequestDTO dto) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada"));
        turma.setPeriodo(dto.periodo());
        turma.setDisciplina(buscarDisciplina(dto.codigoDisciplina()));
        turma.setProfessor(buscarProfessor(dto.emailProfessor()));
        return toResponseDTO(turmaRepository.save(turma));
    }

    public void deletar(Long id) {
        if (!turmaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada");
        }
        turmaRepository.deleteById(id);
    }

    private Disciplina buscarDisciplina(Long codigo) {
        return disciplinaRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Disciplina não encontrada"));
    }

    private User buscarProfessor(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Professor não encontrado"));
    }

    private TurmaResponseDTO toResponseDTO(Turma turma) {
        return new TurmaResponseDTO(
                turma.getId(),
                turma.getPeriodo(),
                turma.getDisciplina() != null ? turma.getDisciplina().getNome() : null,
                turma.getProfessor() != null ? turma.getProfessor().getEmail() : null
        );
    }
}