package com.project.qampus.service;

import java.util.List;
import java.util.Optional;

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

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public Answer create(
            String postId,
            AnswerDTO body,
            Authentication authentication) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post não encontrado"
                ));

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuário não encontrado"
            );
        }

        Answer answer = new Answer();

        answer.setContent(body.content());
        answer.setPost(post);
        answer.setUser(user);

        return answerRepository.save(answer);
    }

    public Answer vote(String answerId, VoteType type, User user) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resposta não encontrada"
                ));

        Optional<Vote> existingVote =
                voteRepository.findByUserIdAndAnswerId(
                        user.getId(),
                        answer.getId()
                );

        if (existingVote.isPresent()) {
            handleExistingVote(answer, existingVote.get(), type);
        } else {
            createVote(answer, type, user);
        }

        return answerRepository.save(answer);
    }

    private void handleExistingVote(
            Answer answer,
            Vote vote,
            VoteType type) {

        if (vote.getType() == type) {
            removeVote(answer, vote, type);
            return;
        }

        changeVote(answer, vote, type);
    }

    private void removeVote(
            Answer answer,
            Vote vote,
            VoteType type) {

        updateVoteCount(answer, type, -1);

        voteRepository.delete(vote);
    }

    private void changeVote(
            Answer answer,
            Vote vote,
            VoteType type) {

        updateVoteCount(answer, vote.getType(), -1);
        updateVoteCount(answer, type, 1);

        vote.setType(type);

        voteRepository.save(vote);
    }

    private void createVote(
            Answer answer,
            VoteType type,
            User user) {

        Vote vote = new Vote();

        vote.setUser(user);
        vote.setAnswer(answer);
        vote.setType(type);

        voteRepository.save(vote);

        updateVoteCount(answer, type, 1);
    }

    private void updateVoteCount(
            Answer answer,
            VoteType type,
            int value) {

        if (type == VoteType.LIKE) {
            answer.setUpVotes(
                    answer.getUpVotes() + value
            );
        } else {
            answer.setDownVotes(
                    answer.getDownVotes() + value
            );
        }
    }

    public Answer update(
            String postId,
            String answerId,
            AnswerDTO body,
            @AuthenticationPrincipal User user) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resposta não encontrada"
                ));

        if (!answer.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Essa resposta pertence a outro usuário"
            );
        }

        answer.setContent(body.content());

        return answerRepository.save(answer);
    }

    public void delete(
            String answerId,
            @AuthenticationPrincipal User user) {

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resposta não encontrada"
                ));

        if (!answer.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Essa resposta pertence a outro usuário"
            );
        }

        answerRepository.delete(answer);
    }

    public List<Answer> findByUserId(String userId) {
        return answerRepository.findByUserId(userId);
    }

    public List<Answer> findByPostId(String postId) {
        return answerRepository.findByPostId(postId);
    }
}