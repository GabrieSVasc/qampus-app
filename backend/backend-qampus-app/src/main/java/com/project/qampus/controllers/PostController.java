package com.project.qampus.controllers;

import com.project.qampus.dto.PostDTO;
import com.project.qampus.dto.PostResponseDTO;
import com.project.qampus.model.Post;
import com.project.qampus.model.enums.VoteType;
import com.project.qampus.service.PostService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.qampus.model.User;


@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostDTO body,
            Authentication authentication) {

        Post saved = postService.create(body, authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PostResponseDTO.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getPosts() {
        List<PostResponseDTO> posts = postService.findAll()
                .stream()
                .map(PostResponseDTO::from)
                .toList();

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPosts(
            @PathVariable String id) {

        Post post = postService.findById(id);

        return ResponseEntity.ok(PostResponseDTO.from(post));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable String id,
            @Valid @RequestBody PostDTO body,
            Authentication authentication) {

        Post updated = postService.update(
                id,
                body,
                authentication
        );

        return ResponseEntity.ok(
                PostResponseDTO.from(updated)
        );
    }
    
    
    @PostMapping("/{id}/upvote")
    public ResponseEntity<PostResponseDTO> upvote(@PathVariable String id, @AuthenticationPrincipal User user){
        Post post = postService.vote(id, VoteType.LIKE, user);
        return ResponseEntity.ok(PostResponseDTO.from(post));
    }

    @PostMapping("/{id}/downvote")
    public ResponseEntity<PostResponseDTO> downvote(@PathVariable String id, @AuthenticationPrincipal User user){
        Post post = postService.vote(id, VoteType.DISLIKE, user);
        return ResponseEntity.ok(PostResponseDTO.from(post));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deletePost(@PathVariable String postId, @AuthenticationPrincipal User user) {
        postService.delete(postId, user);

        return ResponseEntity.noContent().build();
    }
}