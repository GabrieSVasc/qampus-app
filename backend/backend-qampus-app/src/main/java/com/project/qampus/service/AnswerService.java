package com.project.qampus.service;

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
import java.util.List;

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

        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found... x.x"));

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        Answer answer = new Answer();

        answer.setContent(body.content());
        answer.setPost(post);
        answer.setUser(user);

        return answerRepository.save(answer);
    }

    public Answer vote(String answerId, VoteType type, User user) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new RuntimeException("resposta não encontrada"));

        Optional<Vote> existingVote = voteRepository.findByUserIdAndAnswerId(user.getId(), answer.getId());

        // Se já existir voto
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();

            // remover voto
            if (vote.getType() == type) {
                if (type == VoteType.LIKE) {
                    answer.setUpVotes(answer.getUpVotes() - 1);
                } else {
                    answer.setDownVotes(answer.getDownVotes() - 1);
                }
                voteRepository.delete(vote);
            }
            // mudar voto
            else {
                if (type == VoteType.LIKE) {
                    answer.setUpVotes(answer.getUpVotes() + 1);
                    answer.setDownVotes(answer.getDownVotes() - 1);
                } else {
                    answer.setDownVotes(answer.getDownVotes() + 1);
                    answer.setUpVotes(answer.getUpVotes() - 1);
                }
                vote.setType(type);
                voteRepository.save(vote);
            }
        }
        // se nao votou
        else {
            Vote vote = new Vote();

            vote.setUser(user);
            vote.setAnswer(answer);
            vote.setType(type);

            voteRepository.save(vote);

            if (type == VoteType.LIKE) {
                answer.setUpVotes(answer.getUpVotes() + 1);
            } else {
                answer.setDownVotes(answer.getDownVotes() + 1);
            }
        }
        return answerRepository.save(answer);
    }
    
    public Answer update(String postId, String answerId, AnswerDTO Body, @AuthenticationPrincipal User user){
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new RuntimeException("Resposta não encontrada"));

        if (!answer.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Essa resposta pertence a outro usuário");
        }

        answer.setContent(Body.content());

        return answerRepository.save(answer);
    }

    public void delete(String postId, String answerId, @AuthenticationPrincipal User user) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new RuntimeException("Resposta não encontrada"));

        if (!answer.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Essa resposta pertence a outro usuário");
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