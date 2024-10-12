package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Comment;
import com.thinkbigdata.clevo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost(Post post);
}