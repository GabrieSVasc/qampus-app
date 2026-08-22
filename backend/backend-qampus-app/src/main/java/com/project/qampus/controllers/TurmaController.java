package com.project.qampus.controllers;

import com.project.qampus.dto.turma.TurmaRequestDTO;
import com.project.qampus.dto.turma.TurmaResponseDTO;
import com.project.qampus.service.TurmaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @GetMapping
    public List<TurmaResponseDTO> listarTodos() {
        return turmaService.listarTodos();
    }

    @GetMapping("/{id}")
    public TurmaResponseDTO buscarPorId(@PathVariable Long id) {
        return turmaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TurmaResponseDTO criar(@RequestBody TurmaRequestDTO dto) {
        return turmaService.criar(dto);
    }

    @PutMapping("/{id}")
    public TurmaResponseDTO atualizar(@PathVariable Long id, @RequestBody TurmaRequestDTO dto) {
        return turmaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        turmaService.deletar(id);
    }
}