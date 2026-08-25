package com.project.qampus.controllers;

import com.project.qampus.dto.AnswerResponseDTO;
import com.project.qampus.dto.PostDTO;
import com.project.qampus.dto.PostResponseDTO;
import com.project.qampus.model.Answer;
import com.project.qampus.model.Post;
import com.project.qampus.model.User;
import com.project.qampus.model.enums.Role;
import com.project.qampus.model.enums.VoteType;
import com.project.qampus.service.AnswerService;
import com.project.qampus.service.PostService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private AnswerService answerService;

    @Mock
    private Authentication authentication;

    @Mock
    private User user;

    @InjectMocks
    private PostController postController;

    private Post buildPost(String id, String title) {
        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setContent("Conteúdo de exemplo");
        post.setUpVotes(0L);
        post.setDownVotes(0L);
        post.setTags(Set.of());

        User author = new User();
        author.setId("user-1");
        author.setName("Autor");
        author.setEmail("autor@example.com");
        author.setRole(Role.STUDENT);
        post.setUser(author);

        return post;
    }

    @Test
    void shouldSearchPostsSuccessfully() {

        Post post = buildPost("post-1", "Título encontrado");

        when(postService.searchPost("java"))
                .thenReturn(List.of(post));

        var response = postController.searchPosts("java");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(postService).searchPost("java");
    }

    @Test
    void shouldReturnEmptyListWhenSearchFindsNothing() {

        when(postService.searchPost("inexistente"))
                .thenReturn(List.of());

        var response = postController.searchPosts("inexistente");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(postService).searchPost("inexistente");
    }

    @Test
    void shouldCreatePostSuccessfully() {

        PostDTO dto = new PostDTO(
                "Novo post",
                "Conteúdo do novo post",
                Set.of("java", "spring"));

        Post saved = buildPost("post-1", dto.title());

        when(postService.create(dto, authentication))
                .thenReturn(saved);

        var response = postController.createPost(dto, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService).create(dto, authentication);
    }

    @Test
    void shouldGetAllPostsWhenNoFilterProvided() {

        Post post = buildPost("post-1", "Post sem filtro");

        when(postService.findAll())
                .thenReturn(List.of(post));

        var response = postController.getPosts(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(postService).findAll();
        verify(postService, never()).findByTag(any());
    }

    @Test
    void shouldGetPostsFilteredByTagWhenTagProvided() {

        Post post = buildPost("post-1", "Post com tag");

        when(postService.findByTag("java"))
                .thenReturn(List.of(post));

        var response = postController.getPosts("java", "ignorada");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService).findByTag("java");
        verify(postService, never()).findAll();
    }

    @Test
    void shouldGetPostsFilteredByCategoryWhenTagIsBlankButCategoryProvided() {

        Post post = buildPost("post-1", "Post com categoria");

        when(postService.findByTag("categoria-x"))
                .thenReturn(List.of(post));

        var response = postController.getPosts("", "categoria-x");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService).findByTag("categoria-x");
        verify(postService, never()).findAll();
    }

    @Test
    void shouldGetAllPostsWhenTagAndCategoryAreBlank() {

        Post post = buildPost("post-1", "Post genérico");

        when(postService.findAll())
                .thenReturn(List.of(post));

        var response = postController.getPosts("   ", "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService).findAll();
        verify(postService, never()).findByTag(any());
    }

    @Test
    void shouldGetPostById() {

        Post post = buildPost("post-1", "Post específico");

        when(postService.findById("post-1"))
                .thenReturn(post);

        var response = postController.getPost("post-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService).findById("post-1");
    }

    @Test
    void shouldUpdatePostSuccessfully() {

        PostDTO dto = new PostDTO(
                "Título atualizado",
                "Conteúdo atualizado",
                Set.of("atualizado"));

        Post updated = buildPost("post-1", dto.title());

        when(postService.update("post-1", dto, authentication))
                .thenReturn(updated);

        var response = postController.updatePost("post-1", dto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService).update("post-1", dto, authentication);
    }

    @Test
    void shouldUpvotePostSuccessfully() {

        Post post = buildPost("post-1", "Post votado");
        post.setUpVotes(1L);

        when(postService.vote("post-1", VoteType.LIKE, user))
                .thenReturn(post);

        var response = postController.upvote("post-1", user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService).vote("post-1", VoteType.LIKE, user);
    }

    @Test
    void shouldDownvotePostSuccessfully() {

        Post post = buildPost("post-1", "Post votado negativamente");
        post.setDownVotes(1L);

        when(postService.vote("post-1", VoteType.DISLIKE, user))
                .thenReturn(post);

        var response = postController.downvote("post-1", user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(postService).vote("post-1", VoteType.DISLIKE, user);
    }

    @Test
    void shouldDeletePostSuccessfully() {

        var response = postController.deletePost("post-1", user);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(postService).delete("post-1", user);
    }

    @Test
    void shouldGetAnswersByPostSuccessfully() {

        User answerAuthor = new User();
        answerAuthor.setId("user-2");
        answerAuthor.setName("Autor da resposta");
        answerAuthor.setEmail("resposta@example.com");
        answerAuthor.setRole(Role.STUDENT);

        Answer answer = mock(Answer.class);
        when(answer.getUser()).thenReturn(answerAuthor);
        when(answer.getPost()).thenReturn(buildPost("post-1", "Post da resposta"));

        when(answerService.findByPostId("post-1"))
                .thenReturn(List.of(answer));

        var response = postController.getAnswersByPost("post-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(answerService).findByPostId("post-1");
    }

    @Test
    void shouldReturnEmptyListWhenPostHasNoAnswers() {

        when(answerService.findByPostId("post-1"))
                .thenReturn(List.of());

        var response = postController.getAnswersByPost("post-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(answerService).findByPostId("post-1");
    }
}