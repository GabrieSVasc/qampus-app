package com.project.qampus.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.project.qampus.dto.PostDTO;
import com.project.qampus.model.Post;
import com.project.qampus.model.User;
import com.project.qampus.model.Vote;
import com.project.qampus.model.enums.VoteType;
import com.project.qampus.repositories.PostRepository;
import com.project.qampus.repositories.UserRepository;
import com.project.qampus.repositories.VoteRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock PostRepository repository;
    @Mock TagService tagService;
    @Mock UserRepository userRepository;
    @Mock Authentication authentication;
    @Mock VoteRepository voteRepository;
    @Mock Post post;

    @InjectMocks PostService postService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-id");
        user.setName("Leonardo");
        user.setEmail("leonardo@qampus.com");
    }

    @Test
    void shouldCreatePostSuccessfully() {
        PostDTO dto = new PostDTO("Título", "Conteúdo", Set.of());

        when(authentication.getPrincipal()).thenReturn(user);
        when(tagService.resolveTags(Set.of())).thenReturn(Set.of());

        Post saved = new Post();
        saved.setTitle("Título");
        saved.setContent("Conteúdo");
        saved.setUser(user);
        saved.setTags(Set.of());

        when(repository.save(any(Post.class))).thenReturn(saved);

        Post result = postService.create(dto, authentication);

        assertEquals("Título", result.getTitle());
        assertEquals("Conteúdo", result.getContent());
        assertEquals(user, result.getUser());

        verify(authentication).getPrincipal();
        verify(tagService).resolveTags(Set.of());
        verify(repository).save(any(Post.class));
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldFindAllPosts() {
        Post p1 = new Post();
        p1.setTitle("Post 1");

        Post p2 = new Post();
        p2.setTitle("Post 2");

        when(repository.findAllOrderByVotes()).thenReturn(List.of(p1, p2));

        var result = postService.findAll();

        assertEquals(2, result.size());
        assertEquals("Post 1", result.get(0).getTitle());
        assertEquals("Post 2", result.get(1).getTitle());

        verify(repository).findAllOrderByVotes();
    }

    @Test
    void shouldFindPostById() {
        post = new Post();
        post.setTitle("Meu post");

        when(repository.findById("1")).thenReturn(Optional.of(post));

        Post result = postService.findById("1");

        assertEquals("Meu post", result.getTitle());
        verify(repository).findById("1");
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> postService.findById("999")
        );

        assertEquals("Post not found... x.x", e.getMessage());
        verify(repository).findById("999");
    }

    @Test
    void shouldUpdatePostSuccessfully() {
        Set<String> tags = Set.of("Java", "Spring");
        PostDTO dto = new PostDTO("Novo título", "Novo conteúdo", tags);

        when(repository.findById("post-1")).thenReturn(Optional.of(post));
        when(authentication.getPrincipal()).thenReturn(user);
        when(post.getUser()).thenReturn(user);
        when(tagService.resolveTags(tags)).thenReturn(Set.of());
        when(repository.save(post)).thenReturn(post);

        Post result = postService.update("post-1", dto, authentication);

        assertSame(post, result);
        verify(post).setTitle("Novo título");
        verify(post).setContent("Novo conteúdo");
        verify(post).setTags(any());
        verify(tagService).resolveTags(tags);
        verify(repository).save(post);
    }

    @Test
    void shouldThrowExceptionWhenVotingOnNonExistingPost() {
        when(repository.findById("post-404")).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> postService.upvote("post-404", user)
        );

        assertEquals("post não encontrado", e.getMessage());
        verify(repository).findById("post-404");
        verifyNoInteractions(voteRepository);
    }

    @Test
    void shouldCreateLikeWhenUserHasNotVoted() {
        when(repository.findById("post-1")).thenReturn(Optional.of(post));
        when(post.getId()).thenReturn("post-1");
        when(voteRepository.findByUserIdAndPostId("user-id", "post-1"))
                .thenReturn(Optional.empty());
        when(post.getUpVotes()).thenReturn(5L);
        when(repository.save(post)).thenReturn(post);

        assertSame(post, postService.upvote("post-1", user));

        verify(post).setUpVotes(6L);
        verify(voteRepository).save(any(Vote.class));
        verify(repository).save(post);
    }

    @Test
    void shouldRemoveLikeWhenUserVotesAgainWithSameType() {
        Vote vote = new Vote();
        vote.setType(VoteType.LIKE);

        when(repository.findById("post-1")).thenReturn(Optional.of(post));
        when(post.getId()).thenReturn("post-1");
        when(voteRepository.findByUserIdAndPostId("user-id", "post-1"))
                .thenReturn(Optional.of(vote));
        when(post.getUpVotes()).thenReturn(5L);
        when(repository.save(post)).thenReturn(post);

        assertSame(post, postService.upvote("post-1", user));

        verify(post).setUpVotes(4L);
        verify(voteRepository).delete(vote);
        verify(repository).save(post);
    }

    @Test
    void shouldRemoveDislikeWhenUserVotesAgainWithSameType() {
        Vote vote = new Vote();
        vote.setType(VoteType.DISLIKE);

        when(repository.findById("post-1")).thenReturn(Optional.of(post));
        when(post.getId()).thenReturn("post-1");
        when(voteRepository.findByUserIdAndPostId("user-id", "post-1"))
                .thenReturn(Optional.of(vote));
        when(post.getDownVotes()).thenReturn(3L);
        when(repository.save(post)).thenReturn(post);

        assertSame(post, postService.downvote("post-1", user));

        verify(post).setDownVotes(2L);
        verify(voteRepository).delete(vote);
        verify(repository).save(post);
    }

    @Test
    void shouldChangeLikeToDislike() {
        Vote vote = new Vote();
        vote.setType(VoteType.LIKE);

        when(repository.findById("post-1")).thenReturn(Optional.of(post));
        when(post.getId()).thenReturn("post-1");
        when(voteRepository.findByUserIdAndPostId("user-id", "post-1"))
                .thenReturn(Optional.of(vote));
        when(post.getUpVotes()).thenReturn(5L);
        when(post.getDownVotes()).thenReturn(2L);
        when(repository.save(post)).thenReturn(post);

        assertSame(post, postService.downvote("post-1", user));

        verify(post).setUpVotes(4L);
        verify(post).setDownVotes(3L);
        assertEquals(VoteType.DISLIKE, vote.getType());
        verify(voteRepository).save(vote);
        verify(repository).save(post);
    }

    @Test
    void shouldChangeDislikeToLike() {
        Vote vote = new Vote();
        vote.setType(VoteType.DISLIKE);

        when(repository.findById("post-1")).thenReturn(Optional.of(post));
        when(post.getId()).thenReturn("post-1");
        when(voteRepository.findByUserIdAndPostId("user-id", "post-1"))
                .thenReturn(Optional.of(vote));
        when(post.getUpVotes()).thenReturn(5L);
        when(post.getDownVotes()).thenReturn(2L);
        when(repository.save(post)).thenReturn(post);

        assertSame(post, postService.upvote("post-1", user));

        verify(post).setUpVotes(6L);
        verify(post).setDownVotes(1L);
        assertEquals(VoteType.LIKE, vote.getType());
        verify(voteRepository).save(vote);
        verify(repository).save(post);
    }

    @Test
    void shouldCreateDislikeWhenUserHasNotVoted() {
        when(repository.findById("post-1")).thenReturn(Optional.of(post));
        when(post.getId()).thenReturn("post-1");
        when(voteRepository.findByUserIdAndPostId("user-id", "post-1"))
                .thenReturn(Optional.empty());
        when(post.getDownVotes()).thenReturn(3L);
        when(repository.save(post)).thenReturn(post);

        assertSame(post, postService.downvote("post-1", user));

        verify(post).setDownVotes(4L);
        verify(voteRepository).save(any(Vote.class));
        verify(repository).save(post);
    }

    @Test
    void shouldSearchPosts() {
        Post p = new Post();
        p.setTitle("Spring Boot Test");

        when(repository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                "Spring", "Spring")).thenReturn(List.of(p));

        var result = postService.searchPost("Spring");

        assertEquals(1, result.size());
        assertEquals("Spring Boot Test", result.get(0).getTitle());

        verify(repository).findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                "Spring", "Spring");
    }

    @Test
    void shouldFindPostsByUserId() {
        Post p = new Post();
        p.setTitle("User Post");

        when(repository.findByUserId("user-1")).thenReturn(List.of(p));

        var result = postService.findByUserId("user-1");

        assertEquals(1, result.size());
        verify(repository).findByUserId("user-1");
    }

    @Test
    void shouldFindByTagOrderedByVotes() {
        Post p = new Post();
        p.setTitle("Tag Post");

        when(repository.findByTagOrderByVotes("java")).thenReturn(List.of(p));

        var result = postService.findByTag("java");

        assertEquals(1, result.size());
        verify(repository).findByTagOrderByVotes("java");
    }

    @Test
    void shouldFindAllOrderedByVotesWhenTagIsBlank() {
        Post p = new Post();
        p.setTitle("All Post");

        when(repository.findAllOrderByVotes()).thenReturn(List.of(p));

        var result = postService.findByTag("  ");

        assertEquals(1, result.size());
        verify(repository).findAllOrderByVotes();
    }
}