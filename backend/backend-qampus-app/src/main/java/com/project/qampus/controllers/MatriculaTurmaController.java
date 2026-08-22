package com.project.qampus.controllers;

import com.project.qampus.dto.matriculaturma.MatriculaTurmaRequestDTO;
import com.project.qampus.dto.matriculaturma.MatriculaTurmaResponseDTO;
import com.project.qampus.service.MatriculaTurmaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matriculas-turma")
public class MatriculaTurmaController {

    private final MatriculaTurmaService matriculaTurmaService;

    public MatriculaTurmaController(MatriculaTurmaService matriculaTurmaService) {
        this.matriculaTurmaService = matriculaTurmaService;
    }

    @GetMapping
    public List<MatriculaTurmaResponseDTO> listarTodos() {
        return matriculaTurmaService.listarTodos();
    }

    @GetMapping("/{idTurma}/{idAluno}")
    public MatriculaTurmaResponseDTO buscarPorId(@PathVariable Long idTurma, @PathVariable Long idAluno) {
        return matriculaTurmaService.buscarPorId(idTurma, idAluno);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatriculaTurmaResponseDTO criar(@RequestBody MatriculaTurmaRequestDTO dto) {
        return matriculaTurmaService.criar(dto);
    }

    @DeleteMapping("/{idTurma}/{idAluno}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long idTurma, @PathVariable Long idAluno) {
        matriculaTurmaService.deletar(idTurma, idAluno);
    }
}