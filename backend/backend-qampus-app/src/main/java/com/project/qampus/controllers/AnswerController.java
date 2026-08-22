package com.project.qampus.controllers;

import com.project.qampus.dto.AnswerDTO;
import com.project.qampus.dto.AnswerResponseDTO;
import com.project.qampus.model.Answer;
import com.project.qampus.model.enums.VoteType;
import com.project.qampus.service.AnswerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.project.qampus.model.User;

@RestController
@RequestMapping("/post/{postId}/answer")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnswerResponseDTO> createAnswer(
            @PathVariable String postId,
            @Valid @RequestBody AnswerDTO body,
            Authentication authentication) {
                System.out.println("entrou");
        Answer answer = answerService.create(
                postId,
                body,
                authentication
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AnswerResponseDTO.from(answer));
    }

    @PostMapping("/{id}/upvote")
    public ResponseEntity<AnswerResponseDTO> upvote(@PathVariable String id, @AuthenticationPrincipal User user){
        Answer answer = answerService.vote(id, VoteType.LIKE, user);
        return ResponseEntity.ok(AnswerResponseDTO.from(answer));
    }

    @PostMapping("/{id}/downvote")
    public ResponseEntity<AnswerResponseDTO> downvote(@PathVariable String id, @AuthenticationPrincipal User user){
        Answer answer = answerService.vote(id, VoteType.DISLIKE, user);
        return ResponseEntity.ok(AnswerResponseDTO.from(answer));
    }

    @PutMapping("/{answerId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AnswerResponseDTO> updateAnswer(@PathVariable String answerId, @PathVariable String postId, 
                         @Valid @RequestBody AnswerDTO body, @AuthenticationPrincipal User user) {
        
        Answer newAnswer = answerService.update(postId, answerId, body, user);

        return ResponseEntity.ok(AnswerResponseDTO.from(newAnswer));
    }

    @DeleteMapping("/{answerId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteAnswer(@PathVariable String answerId, @PathVariable String postId, 
                                             @AuthenticationPrincipal User user) {

        answerService.delete(postId, answerId, user);

        return ResponseEntity.noContent().build();
    }
}