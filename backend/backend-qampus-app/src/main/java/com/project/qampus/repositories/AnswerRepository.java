package com.project.qampus.repositories;

import com.project.qampus.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, String> {
    List<Answer> findByUserId(String userId);
    List<Answer> findByPostId(String postId);
}