package com.project.qampus.controllers;

import com.project.qampus.dto.AnswerDTO;
import com.project.qampus.dto.AnswerResponseDTO;
import com.project.qampus.model.comentario;
import com.project.qampus.model.Post;
import com.project.qampus.model.User;
import com.project.qampus.service.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class comentarioControllerTest {

    @Mock
    private AnswerService answerService;

    @Mock
    private Authentication authentication;

    @Mock
    private User user;

    @Mock
    private Post post;

    @InjectMocks
    private AnswerController answerController;

    private comentario comentario;

    @BeforeEach
    void setUp() {
        comentario = new comentario();

        comentario.setId("answer-1");
        comentario.setContent("Esta é uma resposta válida.");
        comentario.setUser(user);
        comentario.setPost(post);
    }

    @Test
    void shouldCreateAnswerSuccessfully() {

        String postId = "post-1";

        AnswerDTO answerDTO = new AnswerDTO(
                "Esta é uma resposta válida."
        );

        when(answerService.create(
                postId,
                answerDTO,
                authentication
        )).thenReturn(comentario);

        when(user.getId()).thenReturn("user-1");
        when(post.getId()).thenReturn(postId);

        ResponseEntity<AnswerResponseDTO> response =
                answerController.createAnswer(
                        postId,
                        answerDTO,
                        authentication
                );

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "answer-1",
                response.getBody().id()
        );

        assertEquals(
                "Esta é uma resposta válida.",
                response.getBody().content()
        );

        assertEquals(
                "user-1",
                response.getBody().userId()
        );

        assertEquals(
                "post-1",
                response.getBody().postId()
        );

        verify(answerService).create(
                postId,
                answerDTO,
                authentication
        );
    }

    @Test
    void shouldCallAnswerServiceWithCorrectData() {

        String postId = "post-1";

        AnswerDTO answerDTO = new AnswerDTO(
                "Minha resposta."
        );

        when(answerService.create(
                postId,
                answerDTO,
                authentication
        )).thenReturn(comentario);

        when(user.getId()).thenReturn("user-1");
        when(post.getId()).thenReturn(postId);

        answerController.createAnswer(
                postId,
                answerDTO,
                authentication
        );

        verify(answerService, times(1)).create(
                postId,
                answerDTO,
                authentication
        );
    }

    @Test
    void shouldPropagateExceptionWhenPostDoesNotExist() {

        String postId = "post-404";

        AnswerDTO answerDTO = new AnswerDTO(
                "Minha resposta."
        );

        when(answerService.create(
                postId,
                answerDTO,
                authentication
        )).thenThrow(
                new RuntimeException("Post not found... x.x")
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> answerController.createAnswer(
                        postId,
                        answerDTO,
                        authentication
                )
        );

        assertEquals(
                "Post not found... x.x",
                exception.getMessage()
        );

        verify(answerService).create(
                postId,
                answerDTO,
                authentication
        );
    }

    @Test
    void shouldPropagateExceptionWhenUserDoesNotExist() {

        String postId = "post-1";

        AnswerDTO answerDTO = new AnswerDTO(
                "Minha resposta."
        );

        when(answerService.create(
                postId,
                answerDTO,
                authentication
        )).thenThrow(
                new RuntimeException("Usuário não encontrado.")
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> answerController.createAnswer(
                        postId,
                        answerDTO,
                        authentication
                )
        );

        assertEquals(
                "Usuário não encontrado.",
                exception.getMessage()
        );

        verify(answerService).create(
                postId,
                answerDTO,
                authentication
        );
    }
}