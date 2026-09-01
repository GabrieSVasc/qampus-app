package com.project.qampus.service;

import com.project.qampus.dto.AnswerDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

        @Mock private AnswerRepository answerRepository;
        @Mock private PostRepository postRepository;
        @Mock private VoteRepository voteRepository;
        @Mock private Authentication authentication;

        @InjectMocks
        private AnswerService answerService;

        private User user;
        private Post post;

        @BeforeEach
        void setup() {
                user = new User();
                user.setId("user-1");

                post = new Post();
                post.setId("post-1");
        }



        @Test
        void shouldCreateAnswerSuccessfully() {
                AnswerDTO dto = new AnswerDTO("Resposta válida");

                when(postRepository.findById("post-1"))
                        .thenReturn(Optional.of(post));

                when(authentication.getPrincipal())
                        .thenReturn(user);

                when(answerRepository.save(any(Answer.class)))
                        .thenAnswer(inv -> inv.getArgument(0));

                Answer result = answerService.create("post-1", dto, authentication);

                assertEquals("Resposta válida", result.getContent());
                assertEquals(user, result.getUser());
                assertEquals(post, result.getPost());
        }

        @Test
        void shouldThrowWhenPostNotFound() {
                when(postRepository.findById("post-1"))
                        .thenReturn(Optional.empty());

                assertThrows(
                        ResponseStatusException.class,
                        () -> answerService.create(
                                "post-1",
                                new AnswerDTO("x"),
                                authentication
                        )
                );
        }

        @Test
        void shouldThrowWhenUserIsNull() {
                when(postRepository.findById("post-1"))
                        .thenReturn(Optional.of(post));

                when(authentication.getPrincipal())
                        .thenReturn(null);

                assertThrows(
                        ResponseStatusException.class,
                        () -> answerService.create(
                                "post-1",
                                new AnswerDTO("x"),
                                authentication
                        )
                );
        }

        // ================= VOTE =================

        @Test
        void shouldThrowWhenAnswerNotFoundOnVote() {
                when(answerRepository.findById("answer-404"))
                        .thenReturn(Optional.empty());

                assertThrows(
                        ResponseStatusException.class,
                        () -> answerService.vote("answer-404", VoteType.LIKE, user)
                );
        }

        @Test
        void shouldChangeDislikeToLike() {
                Answer answer = new Answer();
                answer.setId("answer-1");
                answer.setUpVotes(5L);
                answer.setDownVotes(2L);

                Vote vote = new Vote();
                vote.setType(VoteType.DISLIKE);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                when(voteRepository.findByUserIdAndAnswerId("user-1", "answer-1"))
                        .thenReturn(Optional.of(vote));

                answerService.vote("answer-1", VoteType.LIKE, user);

                assertEquals(6L, answer.getUpVotes());
                assertEquals(1L, answer.getDownVotes());
                assertEquals(VoteType.LIKE, vote.getType());

                verify(voteRepository).save(vote);
        }

        @Test
        void shouldRemoveDislikeWhenSameVote() {
                Answer answer = new Answer();
                answer.setId("answer-1");
                answer.setDownVotes(2L);

                Vote vote = new Vote();
                vote.setType(VoteType.DISLIKE);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                when(voteRepository.findByUserIdAndAnswerId("user-1", "answer-1"))
                        .thenReturn(Optional.of(vote));

                answerService.vote("answer-1", VoteType.DISLIKE, user);

                assertEquals(1L, answer.getDownVotes());

                verify(voteRepository).delete(vote);
        }



        @Test
        void shouldUpdateAnswerSuccessfully() {
                Answer answer = new Answer();
                answer.setUser(user);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                when(answerRepository.save(answer))
                        .thenReturn(answer);

                AnswerDTO dto = new AnswerDTO("Novo conteúdo");

                Answer result = answerService.update("answer-1", dto, user);

                assertEquals("Novo conteúdo", result.getContent());
        }

        @Test
        void shouldThrowForbiddenWhenUpdatingOtherUser() {
                User other = new User();
                other.setId("user-2");

                Answer answer = new Answer();
                answer.setUser(other);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                assertThrows(
                        RuntimeException.class,
                        () -> answerService.update(
                                "answer-1",
                                new AnswerDTO("x"),
                                user
                        )
                );
        }

        @Test
        void shouldThrowWhenUpdatingNotFound() {
                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.empty());

                assertThrows(
                        RuntimeException.class,
                        () -> answerService.update(
                                "answer-1",
                                new AnswerDTO("x"),
                                user
                        )
                );
        }



        @Test
        void shouldDeleteSuccessfully() {
                Answer answer = new Answer();
                answer.setUser(user);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                answerService.delete("answer-1", user);

                verify(answerRepository).delete(answer);
        }

        @Test
        void shouldThrowWhenDeleteNotFound() {
                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.empty());

                assertThrows(
                        RuntimeException.class,
                        () -> answerService.delete("answer-1", user)
                );
        }

        @Test
        void shouldThrowWhenDeleteForbidden() {
                User other = new User();
                other.setId("user-2");

                Answer answer = new Answer();
                answer.setUser(other);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                assertThrows(
                        RuntimeException.class,
                        () -> answerService.delete("answer-1", user)
                );
        }



        @Test
        void shouldFindByUserId() {
                Answer a = new Answer();

                when(answerRepository.findByUserId("user-1"))
                        .thenReturn(List.of(a));

                assertEquals(1, answerService.findByUserId("user-1").size());
        }

        @Test
        void shouldFindByPostId() {
                Answer a = new Answer();

                when(answerRepository.findByPostId("post-1"))
                        .thenReturn(List.of(a));

                assertEquals(1, answerService.findByPostId("post-1").size());
        }
}