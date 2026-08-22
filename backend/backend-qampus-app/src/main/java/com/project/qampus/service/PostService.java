package com.project.qampus.service;

import java.util.List;
import java.util.Optional;

import com.project.qampus.dto.PostDTO;
import com.project.qampus.model.Post;
import com.project.qampus.model.User;
import com.project.qampus.model.Vote;
import com.project.qampus.model.enums.VoteType;
import com.project.qampus.repositories.PostRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.qampus.repositories.VoteRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final TagService tagService;
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;

    public Post create(PostDTO body, Authentication authentication) {

        Post post = new Post();

        post.setTitle(body.title());
        post.setContent(body.content());
        post.setTags(tagService.resolveTags(body.tags()));

        User user = (User) authentication.getPrincipal();

        post.setUser(user);

        return repository.save(post);
    }

    public List<Post> findAll() {
        return repository.findAll();
    }

    public Post findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found... x.x"));
    }

    public Post update(
            String id,
            PostDTO body,
            Authentication authentication) {

        Post post = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found... x.x"));

        User user = (User) authentication.getPrincipal();

        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Você não pode editar esta dúvida.");
        }

        post.setTitle(body.title());
        post.setContent(body.content());
        post.setTags(tagService.resolveTags(body.tags()));

        return repository.save(post);
    }

    public void delete(String postId, @AuthenticationPrincipal User user) {
        Post post = postRepository.findById(postId).orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND,"Post não encontrado"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Esse post pertence a outro usuário");
        }
        
        postRepository.delete(post);
    }

    public Post vote(String postId, VoteType type, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("post não encontrado"));

        Optional<Vote> existingVote = voteRepository.findByUserIdAndPostId(user.getId(), post.getId());

        // Se já existir voto
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();

            // remover voto
            if (vote.getType() == type) {
                if (type == VoteType.LIKE) {
                    post.setUpVotes(post.getUpVotes() - 1);
                } else {
                    post.setDownVotes(post.getDownVotes() - 1);
                }
                voteRepository.delete(vote);
            }
            // mudar voto
            else {
                if (type == VoteType.LIKE) {
                    post.setUpVotes(post.getUpVotes() + 1);
                    post.setDownVotes(post.getDownVotes() - 1);
                } else {
                    post.setDownVotes(post.getDownVotes() + 1);
                    post.setUpVotes(post.getUpVotes() - 1);
                }
                vote.setType(type);
                voteRepository.save(vote);
            }
        }
        // se nao votou
        else {
            Vote vote = new Vote();

            vote.setUser(user);
            vote.setPost(post);
            vote.setType(type);

            voteRepository.save(vote);

            if (type == VoteType.LIKE) {
                post.setUpVotes(post.getUpVotes() + 1);
            } else {
                post.setDownVotes(post.getDownVotes() + 1);
            }
        }
        return postRepository.save(post);
    }
}