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

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.qampus.dto.RecommendationResponseDTO;
import com.project.qampus.repositories.VoteRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final VoteRepository voteRepository;

    public Post create(PostDTO body, Authentication authentication) {

        Post post = new Post();

        post.setTitle(body.title());
        post.setContent(body.content());
        post.setTags(tagService.resolveTags(body.tags()));

        User user = (User) authentication.getPrincipal();

        post.setUser(user);

        return postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAllOrderByVotes();
    }

    public Post findById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found... x.x"));
    }

    public Post update(
            String id,
            PostDTO body,
            Authentication authentication) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found... x.x"));

        User user = (User) authentication.getPrincipal();

        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Você não pode editar esta dúvida.");
        }

        post.setTitle(body.title());
        post.setContent(body.content());
        post.setTags(tagService.resolveTags(body.tags()));

        return postRepository.save(post);
    }

    public void delete(String postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esse post pertence a outro usuário");
        }

        postRepository.delete(post);
    }

    public Post upvote(String postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("post não encontrado"));

        Optional<Vote> existingVote = voteRepository.findByUserIdAndPostId(user.getId(), post.getId());

        // Se já existir voto
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();

            // remover voto
            if (vote.getType() == VoteType.LIKE) {
                post.setUpVotes(post.getUpVotes() - 1);
                voteRepository.delete(vote);
            }
            // mudar voto
            else {
                post.setUpVotes(post.getUpVotes() + 1);
                post.setDownVotes(post.getDownVotes() - 1);
                vote.setType(VoteType.LIKE);
                voteRepository.save(vote);
            }
        }
        // se nao votou
        else {
            Vote vote = new Vote();

            vote.setUser(user);
            vote.setPost(post);
            vote.setType(VoteType.LIKE);

            voteRepository.save(vote);
            post.setUpVotes(post.getUpVotes() + 1);
        }
        return postRepository.save(post);
    }

    public Post downvote(String postId, User user) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("post não encontrado"));

        Optional<Vote> existingVote = voteRepository.findByUserIdAndPostId(user.getId(), post.getId());

        // Se já existir voto
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();

            // remover voto
            if (vote.getType() == VoteType.DISLIKE) {
                post.setDownVotes(post.getDownVotes() - 1);
                voteRepository.delete(vote);
            }
            // mudar voto
            else {
                post.setDownVotes(post.getDownVotes() + 1);
                post.setUpVotes(post.getUpVotes() - 1);
                vote.setType(VoteType.DISLIKE);
                voteRepository.save(vote);
            }
        }
        // se nao votou
        else {
            Vote vote = new Vote();

            vote.setUser(user);
            vote.setPost(post);
            vote.setType(VoteType.DISLIKE);

            voteRepository.save(vote);
            post.setDownVotes(post.getDownVotes() + 1);
        }
        return postRepository.save(post);
    }

    public List<Post> findByUserId(String userId) {
        return postRepository.findByUserId(userId);
    }

    public List<Post> searchPost(String busca) {
        String busco = busca;
        String busquei = busco;
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(busca, busquei);
    }

    public List<Post> findByTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            return postRepository.findByTagOrderByVotes(tag.trim());
        }
        return postRepository.findAllOrderByVotes();
    }

    // The findByCategory method was redundant; removed.
    // The explicit findAllOrderByVotes wrapper is also redundant; callers use
    // findAll() directly.

    public List<RecommendationResponseDTO> recommend(String postId) {
        postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Post não encontrado"));

        return postRepository.findRecommendedPosts(postId).stream().map(post -> 
                new RecommendationResponseDTO(post.getId(), post.getTitle(), post.getUpVotes() - post.getDownVotes())).toList();
    }

}