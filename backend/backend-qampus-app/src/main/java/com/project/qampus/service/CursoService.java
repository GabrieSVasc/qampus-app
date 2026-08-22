package com.project.qampus.service;

import com.project.qampus.dto.CursoRequestDTO;
import com.project.qampus.dto.CursoResponseDTO;
import com.project.qampus.model.Curso;
import com.project.qampus.model.User;
import com.project.qampus.repositories.CursoRepository;
import com.project.qampus.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UserRepository userRepository;

    public CursoService(CursoRepository cursoRepository, UserRepository userRepository) {
        this.cursoRepository = cursoRepository;
        this.userRepository = userRepository;
    }

    public List<CursoResponseDTO> listarTodos() {
        return cursoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CursoResponseDTO buscarPorId(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        return toResponseDTO(curso);
    }

    public CursoResponseDTO criar(CursoRequestDTO dto) {
        Curso curso = new Curso();
        curso.setBloco(dto.bloco());
        curso.setNome(dto.nome());
        curso.setCoordenador(buscarCoordenador(dto.email()));
        return toResponseDTO(cursoRepository.save(curso));
    }

    public CursoResponseDTO atualizar(Long id, CursoRequestDTO dto) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        curso.setBloco(dto.bloco());
        curso.setNome(dto.nome());
        curso.setCoordenador(buscarCoordenador(dto.email()));
        return toResponseDTO(cursoRepository.save(curso));
    }

    public void deletar(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado");
        }
        cursoRepository.deleteById(id);
    }

    private User buscarCoordenador(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordenador não encontrado"));
    }

    private CursoResponseDTO toResponseDTO(Curso curso) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getBloco(),
                curso.getNome(),
                curso.getCoordenador() != null ? curso.getCoordenador().getEmail() : null
        );
    }
}