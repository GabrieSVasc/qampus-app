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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

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
        private Authentication authentication;

        @Mock
        private Answer answer;

        @Mock
        private Post post;

        @Mock
        private User user;

        @InjectMocks
        private AnswerService answerService;

        @Mock
        private VoteRepository voteRepository;

        @Test
        void shouldCreateAnswerSuccessfully() {

                AnswerDTO dto = new AnswerDTO(
                                "Esta é uma resposta válida.");

                when(postRepository.findById("post-1"))
                                .thenReturn(Optional.of(post));

                when(authentication.getPrincipal())
                                .thenReturn(user);

                when(answerRepository.save(any(Answer.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Answer result = answerService.create(
                                "post-1",
                                dto,
                                authentication);

                assertNotNull(result);

                assertEquals(
                                "Esta é uma resposta válida.",
                                result.getContent());

                assertSame(
                                user,
                                result.getUser());

                assertSame(
                                post,
                                result.getPost());

                verify(postRepository)
                                .findById("post-1");

                verify(authentication)
                                .getPrincipal();

                verify(answerRepository)
                                .save(any(Answer.class));

                verifyNoInteractions(userRepository);
        }


        @Test
        void shouldThrowExceptionWhenPostDoesNotExist() {

                AnswerDTO dto = new AnswerDTO(
                        "Minha resposta.");

                when(postRepository.findById("post-1"))
                        .thenReturn(Optional.empty());

                ResponseStatusException exception = assertThrows(
                        ResponseStatusException.class,
                        () -> answerService.create(
                                "post-1",
                                dto,
                                authentication));

                assertEquals(
                        HttpStatus.NOT_FOUND,
                        exception.getStatusCode());

                assertEquals(
                        "Post não encontrado",
                        exception.getReason());

                verify(postRepository)
                        .findById("post-1");

                verifyNoInteractions(userRepository);
                verifyNoInteractions(answerRepository);
                verifyNoInteractions(authentication);
        }

        @Test
        void shouldThrowExceptionWhenAuthenticatedUserDoesNotExist() {

                AnswerDTO dto = new AnswerDTO(
                        "Minha resposta.");

                when(postRepository.findById("post-1"))
                        .thenReturn(Optional.of(post));

                when(authentication.getPrincipal())
                        .thenReturn(null);

                ResponseStatusException exception = assertThrows(
                        ResponseStatusException.class,
                        () -> answerService.create(
                                "post-1",
                                dto,
                                authentication));

                assertEquals(
                        HttpStatus.NOT_FOUND,
                        exception.getStatusCode());

                assertEquals(
                        "Usuário não encontrado",
                        exception.getReason());

                verify(postRepository)
                        .findById("post-1");

                verify(authentication)
                        .getPrincipal();

                verify(answerRepository, never())
                        .save(any(Answer.class));

                verifyNoInteractions(userRepository);
        }

        @Test
        void shouldThrowExceptionWhenAnswerDoesNotExist() {
                when(answerRepository.findById(("answer-404")))
                        .thenReturn(Optional.empty());

                ResponseStatusException exception = assertThrows(
                        ResponseStatusException.class,
                        () -> answerService.vote(
                                "answer-404",
                                VoteType.LIKE,
                                user));

                assertEquals(
                        HttpStatus.NOT_FOUND,
                        exception.getStatusCode());

                assertEquals(
                        "Resposta não encontrada",
                        exception.getReason());

                verify(answerRepository)
                        .findById("answer-404");

                verifyNoInteractions(voteRepository);
        }

        @Test
        void shouldCreateLikeWhenUserHasNotVoted() {

                when(answerRepository.findById("answer-1"))
                                .thenReturn(Optional.of(answer));

                when(user.getId())
                                .thenReturn("user-1");

                when(answer.getId())
                                .thenReturn("answer-1");

                when(voteRepository.findByUserIdAndAnswerId(
                                "user-1",
                                "answer-1")).thenReturn(Optional.empty());

                when(answer.getUpVotes())
                                .thenReturn(5L);

                when(answerRepository.save(answer))
                                .thenReturn(answer);

                Answer result = answerService.vote(
                                "answer-1",
                                VoteType.LIKE,
                                user);

                assertSame(answer, result);

                verify(voteRepository).save(any(Vote.class));

                verify(answer).setUpVotes(6L);

                verify(answerRepository).save(answer);
        }

        @Test
        void shouldCreateDislikeWhenUserHasNotVoted() {

                when(answerRepository.findById("answer-1"))
                                .thenReturn(Optional.of(answer));

                when(user.getId())
                                .thenReturn("user-1");

                when(answer.getId())
                                .thenReturn("answer-1");

                when(voteRepository.findByUserIdAndAnswerId(
                                "user-1",
                                "answer-1")).thenReturn(Optional.empty());

                when(answer.getDownVotes())
                                .thenReturn(3L);

                when(answerRepository.save(answer))
                                .thenReturn(answer);

                Answer result = answerService.vote(
                                "answer-1",
                                VoteType.DISLIKE,
                                user);

                assertSame(answer, result);

                verify(voteRepository).save(any(Vote.class));

                verify(answer).setDownVotes(4L);

                verify(answerRepository).save(answer);
        }

        @Test
        void shouldRemoveLikeWhenUserVotesAgainWithSameType() {

                when(answerRepository.findById("answer-1"))
                                .thenReturn(Optional.of(answer));

                when(user.getId())
                                .thenReturn("user-1");

                when(answer.getId())
                                .thenReturn("answer-1");

                Vote vote = new Vote();
                vote.setType(VoteType.LIKE);

                when(voteRepository.findByUserIdAndAnswerId(
                                "user-1",
                                "answer-1")).thenReturn(Optional.of(vote));

                when(answer.getUpVotes())
                                .thenReturn(5L);

                when(answerRepository.save(answer))
                                .thenReturn(answer);

                Answer result = answerService.vote(
                                "answer-1",
                                VoteType.LIKE,
                                user);

                assertSame(answer, result);

                verify(answer)
                                .setUpVotes(4L);

                verify(voteRepository)
                                .delete(vote);

                verify(answerRepository)
                                .save(answer);
        }

        @Test
        void shouldChangeLikeToDislike() {

                when(answerRepository.findById("answer-1"))
                                .thenReturn(Optional.of(answer));

                when(user.getId())
                                .thenReturn("user-1");

                when(answer.getId())
                                .thenReturn("answer-1");

                Vote vote = new Vote();
                vote.setType(VoteType.LIKE);

                when(voteRepository.findByUserIdAndAnswerId(
                                "user-1",
                                "answer-1")).thenReturn(Optional.of(vote));

                when(answer.getUpVotes())
                                .thenReturn(5L);

                when(answer.getDownVotes())
                                .thenReturn(2L);

                when(answerRepository.save(answer))
                                .thenReturn(answer);

                Answer result = answerService.vote(
                                "answer-1",
                                VoteType.DISLIKE,
                                user);

                assertSame(answer, result);

                verify(answer)
                                .setDownVotes(3L);

                verify(answer)
                                .setUpVotes(4L);

                assertEquals(
                                VoteType.DISLIKE,
                                vote.getType());

                verify(voteRepository)
                                .save(vote);

                verify(voteRepository, never())
                                .delete(vote);

                verify(answerRepository)
                                .save(answer);
        }
}