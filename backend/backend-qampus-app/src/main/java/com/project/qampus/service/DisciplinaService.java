package com.project.qampus.service;


import com.project.qampus.dto.DisciplinaRequestDTO;
import com.project.qampus.dto.DisciplinaResponseDTO;
import com.project.qampus.model.Curso;
import com.project.qampus.model.Disciplina;
import com.project.qampus.repositories.CursoRepository;
import com.project.qampus.repositories.DisciplinaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final CursoRepository cursoRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, CursoRepository cursoRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<DisciplinaResponseDTO> listarTodos() {
        return disciplinaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public DisciplinaResponseDTO buscarPorId(Long codigo) {
        Disciplina disciplina = disciplinaRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada"));
        return toResponseDTO(disciplina);
    }

    public DisciplinaResponseDTO criar(DisciplinaRequestDTO dto) {
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(dto.nome());
        disciplina.setCurso(buscarCurso(dto.idCurso()));
        return toResponseDTO(disciplinaRepository.save(disciplina));
    }

    public DisciplinaResponseDTO atualizar(Long codigo, DisciplinaRequestDTO dto) {
        Disciplina disciplina = disciplinaRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada"));
        disciplina.setNome(dto.nome());
        disciplina.setCurso(buscarCurso(dto.idCurso()));
        return toResponseDTO(disciplinaRepository.save(disciplina));
    }

    public void deletar(Long codigo) {
        if (!disciplinaRepository.existsById(codigo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada");
        }
        disciplinaRepository.deleteById(codigo);
    }

    private Curso buscarCurso(Long idCurso) {
        return cursoRepository.findById(idCurso)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso não encontrado"));
    }

    private DisciplinaResponseDTO toResponseDTO(Disciplina disciplina) {
        return new DisciplinaResponseDTO(
                disciplina.getCodigo(),
                disciplina.getNome(),
                disciplina.getCurso() != null ? disciplina.getCurso().getNome() : null
        );
    }
}