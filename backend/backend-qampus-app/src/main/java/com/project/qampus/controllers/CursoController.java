package com.project.qampus.controllers;
import com.project.qampus.dto.CursoRequestDTO;
import com.project.qampus.dto.CursoResponseDTO;

import com.project.qampus.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public List<CursoResponseDTO> listarTodos() {
        return cursoService.listarTodos();
    }

    @GetMapping("/{id}")
    public CursoResponseDTO buscarPorId(@PathVariable Long id) {
        return cursoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CursoResponseDTO criar(@RequestBody CursoRequestDTO dto) {
        return cursoService.criar(dto);
    }

    @PutMapping("/{id}")
    public CursoResponseDTO atualizar(@PathVariable Long id, @RequestBody CursoRequestDTO dto) {
        return cursoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        cursoService.deletar(id);
    }
}