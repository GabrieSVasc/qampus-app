package com.project.qampus.controllers;
import com.project.qampus.dto.DisciplinaRequestDTO;
import com.project.qampus.dto.DisciplinaResponseDTO;
import com.project.qampus.service.DisciplinaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    @GetMapping
    public List<DisciplinaResponseDTO> listarTodos() {
        return disciplinaService.listarTodos();
    }

    @GetMapping("/{codigo}")
    public DisciplinaResponseDTO buscarPorId(@PathVariable Long codigo) {
        return disciplinaService.buscarPorId(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisciplinaResponseDTO criar(@RequestBody DisciplinaRequestDTO dto) {
        return disciplinaService.criar(dto);
    }

    @PutMapping("/{codigo}")
    public DisciplinaResponseDTO atualizar(@PathVariable Long codigo, @RequestBody DisciplinaRequestDTO dto) {
        return disciplinaService.atualizar(codigo, dto);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long codigo) {
        disciplinaService.deletar(codigo);
    }
}