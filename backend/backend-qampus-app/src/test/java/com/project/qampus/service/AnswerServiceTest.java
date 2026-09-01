package com.project.qampus.service;

import com.project.qampus.dto.AnswerDTO;
import com.project.qampus.model.Answer;
import com.project.qampus.model.Post;
import com.project.qampus.model.User;
import com.project.qampus.model.Vote;
import com.project.qampus.model.enums.VoteType;
import com.project.qampus.repositories.AnswerRepository;
import com.project.qampus.repositories.PostRepository;
import com.project.qampus.repositories.UserRepository;
import com.project.qampus.repositories.VoteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

        @Mock
        private AnswerRepository answerRepository;

        @Mock
        private PostRepository postRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private VoteRepository voteRepository;

        @Mock
        private Authentication authentication;

        @InjectMocks
        private AnswerService answerService;

        private Answer answer;
        private User user;
        private Post post;

        @BeforeEach
        void setup() {
                answer = new Answer();
                answer.setId("answer-1");
                answer.setUpVotes(5L);
                answer.setDownVotes(2L);

                user = new User();
                user.setId("user-1");

                post = new Post();
                post.setId("post-1");
        }

// ================= CREATE =================

        @Test
        void shouldCreateAnswerSuccessfully() {
                AnswerDTO dto = new AnswerDTO("Resposta válida");

                when(postRepository.findById("post-1"))
                        .thenReturn(Optional.of(post));

                when(authentication.getPrincipal())
                        .thenReturn(user);

                when(answerRepository.save(any(Answer.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                Answer result = answerService.create("post-1", dto, authentication);

                assertNotNull(result);
                assertEquals("Resposta válida", result.getContent());
                assertEquals(user, result.getUser());
                assertEquals(post, result.getPost());
        }

        @Test
        void shouldThrowWhenPostNotFound() {
                when(postRepository.findById("post-1"))
                        .thenReturn(Optional.empty());

                ResponseStatusException ex = assertThrows(
                        ResponseStatusException.class,
                        () -> answerService.create("post-1", new AnswerDTO("x"), authentication)
                );

                assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }

        @Test
        void shouldThrowWhenUserIsNull() {
                when(postRepository.findById("post-1"))
                        .thenReturn(Optional.of(post));

                when(authentication.getPrincipal())
                        .thenReturn(null);

                ResponseStatusException ex = assertThrows(
                        ResponseStatusException.class,
                        () -> answerService.create("post-1", new AnswerDTO("x"), authentication)
                );

                assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        }

// ================= VOTE =================

        @Test
        void shouldRemoveDislikeWhenUserClicksAgain() {
                Vote vote = new Vote();
                vote.setType(VoteType.DISLIKE);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                when(voteRepository.findByUserIdAndAnswerId("user-1", "answer-1"))
                        .thenReturn(Optional.of(vote));

                when(answerRepository.save(answer)).thenReturn(answer);

                answerService.vote("answer-1", VoteType.DISLIKE, user);

                assertEquals(1L, answer.getDownVotes());
                verify(voteRepository).delete(vote);
        }

        @Test
        void shouldChangeDislikeToLike() {
                Vote vote = new Vote();
                vote.setType(VoteType.DISLIKE);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                when(voteRepository.findByUserIdAndAnswerId("user-1", "answer-1"))
                        .thenReturn(Optional.of(vote));

                when(answerRepository.save(answer)).thenReturn(answer);

                answerService.vote("answer-1", VoteType.LIKE, user);

                assertEquals(6L, answer.getUpVotes());
                assertEquals(1L, answer.getDownVotes());
                assertEquals(VoteType.LIKE, vote.getType());
        }

        @Test
        void shouldThrowWhenAnswerNotFoundOnVote() {
                when(answerRepository.findById("answer-404"))
                        .thenReturn(Optional.empty());

                assertThrows(ResponseStatusException.class,
                        () -> answerService.vote("answer-404", VoteType.LIKE, user));
        }

// ================= UPDATE =================

        @Test
        void shouldUpdateAnswerSuccessfully() {
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
        void shouldThrowForbiddenWhenUpdatingAnotherUserAnswer() {
                User other = new User();
                other.setId("user-2");

                answer.setUser(other);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                assertThrows(ResponseStatusException.class,
                        () -> answerService.update("answer-1", new AnswerDTO("x"), user));
        }

        @Test
        void shouldThrowNotFoundWhenUpdating() {
                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.empty());

                assertThrows(ResponseStatusException.class,
                        () -> answerService.update("answer-1", new AnswerDTO("x"), user));
        }

// ================= DELETE =================

        @Test
        void shouldDeleteSuccessfully() {
                answer.setUser(user);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                answerService.delete("answer-1", user);

                verify(answerRepository).delete(answer);
        }

        @Test
        void shouldThrowForbiddenOnDelete() {
                User other = new User();
                other.setId("user-2");

                answer.setUser(other);

                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.of(answer));

                assertThrows(ResponseStatusException.class,
                        () -> answerService.delete("answer-1", user));
        }

        @Test
        void shouldThrowNotFoundOnDelete() {
                when(answerRepository.findById("answer-1"))
                        .thenReturn(Optional.empty());

                assertThrows(ResponseStatusException.class,
                        () -> answerService.delete("answer-1", user));
        }

// ================= FIND =================

        @Test
        void shouldFindByUserId() {
                when(answerRepository.findByUserId("user-1"))
                        .thenReturn(List.of(answer));

                List<Answer> result = answerService.findByUserId("user-1");

                assertEquals(1, result.size());
        }

        @Test
        void shouldFindByPostId() {
                when(answerRepository.findByPostId("post-1"))
                        .thenReturn(List.of(answer));

                List<Answer> result = answerService.findByPostId("post-1");

                assertEquals(1, result.size());
        }


}
