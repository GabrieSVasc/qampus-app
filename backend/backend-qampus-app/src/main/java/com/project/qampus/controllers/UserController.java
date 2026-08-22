package com.project.qampus.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.project.qampus.service.UserService;
import com.project.qampus.dto.UserDTO;
import com.project.qampus.dto.UserResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.qampus.model.User;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<String> getUser(){
        return ResponseEntity.ok("SUCESSO");
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String userId, @Valid @RequestBody UserDTO body, 
        @AuthenticationPrincipal User user) {

        User userFinal = userService.update(userId, body, user);

        return ResponseEntity.ok(UserResponseDTO.from(userFinal));
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId, @AuthenticationPrincipal User user) {

        userService.delete(userId, user);

        return ResponseEntity.noContent().build();
    }
}