package com.project.qampus.service;

import com.project.qampus.dto.PostDTO;
import com.project.qampus.dto.RecommendationResponseDTO;
import com.project.qampus.model.*;
import com.project.qampus.model.enums.VoteType;
import com.project.qampus.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock PostRepository repository;
    @Mock TagService tagService;
    @Mock VoteRepository voteRepository;
    @Mock Authentication authentication;

    @InjectMocks PostService postService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId("user-id");
    }



    @Test
    void shouldCreatePostSuccessfully() {
        PostDTO dto = new PostDTO("Título", "Conteúdo", Set.of());

        when(authentication.getPrincipal()).thenReturn(user);
        when(tagService.resolveTags(Set.of())).thenReturn(Set.of());

        when(repository.save(any(Post.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Post result = postService.create(dto, authentication);

        assertEquals("Título", result.getTitle());
        assertEquals("Conteúdo", result.getContent());
        assertEquals(user, result.getUser());
    }



    @Test
    void shouldFindAllPosts() {
        Post p1 = new Post();
        Post p2 = new Post();

        when(repository.findAllOrderByVotes())
                .thenReturn(List.of(p1, p2));

        List<Post> result = postService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldFindById() {
        Post post = new Post();
        post.setTitle("Post");

        when(repository.findById("1"))
                .thenReturn(Optional.of(post));

        Post result = postService.findById("1");

        assertEquals("Post", result.getTitle());
    }

    @Test
    void shouldThrowWhenPostNotFound() {
        when(repository.findById("999"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> postService.findById("999")
        );

        assertEquals("Post not found... x.x", ex.getMessage());
    }



    @Test
    void shouldUpdatePostSuccessfully() {
        Post post = new Post();
        post.setUser(user);

        PostDTO dto = new PostDTO("Novo", "Conteúdo", Set.of());

        when(repository.findById("1")).thenReturn(Optional.of(post));
        when(authentication.getPrincipal()).thenReturn(user);
        when(tagService.resolveTags(Set.of())).thenReturn(Set.of());
        when(repository.save(post)).thenReturn(post);

        Post result = postService.update("1", dto, authentication);

        assertEquals(post, result);
        verify(repository).save(post);
    }

    @Test
    void shouldThrowWhenUpdatingOtherUser() {
        User other = new User();
        other.setId("other");

        Post post = new Post();
        post.setUser(other);

        when(repository.findById("1"))
                .thenReturn(Optional.of(post));

        when(authentication.getPrincipal())
                .thenReturn(user);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> postService.update(
                        "1",
                        new PostDTO("x", "y", Set.of()),
                        authentication
                )
        );

        assertEquals("Você não pode editar esta dúvida.", ex.getMessage());
    }



    @Test
    void shouldDeletePostSuccessfully() {
        Post post = new Post();
        post.setUser(user);

        when(repository.findById("1"))
                .thenReturn(Optional.of(post));

        postService.delete("1", user);

        verify(repository).delete(post);
    }

    @Test
    void shouldThrowWhenDeletingNotFound() {
        when(repository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> postService.delete("1", user)
        );
    }

    @Test
    void shouldThrowWhenDeletingOtherUser() {
        User other = new User();
        other.setId("other");

        Post post = new Post();
        post.setUser(other);

        when(repository.findById("1"))
                .thenReturn(Optional.of(post));

        assertThrows(
                ResponseStatusException.class,
                () -> postService.delete("1", user)
        );
    }

    // ================= SEARCH =================

    @Test
    void shouldSearchPosts() {
        Post p = new Post();
        p.setTitle("Spring Boot Test");

        when(repository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                "Spring", "Spring"))
                .thenReturn(List.of(p));

        List<Post> result = postService.searchPost("Spring");

        assertEquals(1, result.size());
        assertEquals("Spring Boot Test", result.get(0).getTitle());
    }



    @Test
    void shouldFindByTag() {
        Post p = new Post();

        when(repository.findByTagOrderByVotes("java"))
                .thenReturn(List.of(p));

        List<Post> result = postService.findByTag("java");

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindAllWhenTagBlank() {
        when(repository.findAllOrderByVotes())
                .thenReturn(List.of(new Post()));

        List<Post> result = postService.findByTag("  ");

        assertEquals(1, result.size());
    }



    @Test
    void shouldReturnRecommendations() {
        Post p = new Post();
        p.setId("2");
        p.setTitle("Recomendado");
        p.setUpVotes(10L);
        p.setDownVotes(2L);

        when(repository.findById("1"))
                .thenReturn(Optional.of(new Post()));

        when(repository.findRecommendedPosts("1"))
                .thenReturn(List.of(p));

        List<RecommendationResponseDTO> result = postService.recommend("1");

        assertEquals(1, result.size());

        RecommendationResponseDTO dto = result.get(0);

        assertEquals("Recomendado", dto.title());
        assertEquals(8L, dto.voteBalance());
    }

    @Test
    void shouldThrowWhenRecommendNotFound() {
        when(repository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> postService.recommend("1")
        );
    }
}