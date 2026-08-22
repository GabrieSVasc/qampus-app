package com.project.qampus.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.project.qampus.service.UserService;
import com.project.qampus.service.PostService;
import com.project.qampus.service.AnswerService;
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
import java.util.List;

import com.project.qampus.dto.AnswerResponseDTO;
import com.project.qampus.dto.PostResponseDTO;
import com.project.qampus.model.User;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final AnswerService answerService;

    @GetMapping("/{userid}/posts")
    public ResponseEntity<List<PostResponseDTO>> getUserPosts(@PathVariable String userid){
        List<PostResponseDTO> posts = postService.findByUserId(userid).stream().map(PostResponseDTO::from).toList();
        
        return ResponseEntity.ok(posts);
    }

    @GetMapping("{userId}/answers")
    public ResponseEntity<List<AnswerResponseDTO>> userAnswers(@PathVariable String userId) {

        List<AnswerResponseDTO> answers = answerService.findByUserId(userId).stream().map(AnswerResponseDTO::from).toList();

        return ResponseEntity.ok(answers);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String userId) {

        User user = userService.findById(userId);

        return ResponseEntity.ok(UserResponseDTO.from(user));
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