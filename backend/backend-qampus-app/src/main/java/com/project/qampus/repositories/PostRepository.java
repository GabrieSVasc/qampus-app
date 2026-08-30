package com.project.qampus.repositories;

import com.project.qampus.model.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    List<Post> findByUserId(String userId);

    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE LOWER(t.name) = LOWER(:tag) ORDER BY (COALESCE(p.upVotes, 0) - COALESCE(p.downVotes, 0)) DESC, p.upVotes DESC, p.createdAt DESC")
    List<Post> findByTagOrderByVotes(@Param("tag") String tag);

    @Query("SELECT p FROM Post p ORDER BY (COALESCE(p.upVotes, 0) - COALESCE(p.downVotes, 0)) DESC, p.upVotes DESC, p.createdAt DESC")
    List<Post> findAllOrderByVotes();

    @Query("""
            SELECT p
            FROM Post p
            JOIN p.tags t
            WHERE t IN (SELECT tag
                        FROM Post post
                        JOIN post.tags tag
                        WHERE post.id = :postId
            )
            AND p.id <> :postId
            GROUP BY p
            ORDER BY (p.upVotes - p.downVotes) DESC
          """)

    List<Post> findRecommendedPosts(@Param("postId") String postId);
}
