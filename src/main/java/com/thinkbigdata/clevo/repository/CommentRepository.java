package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Comment;
import com.thinkbigdata.clevo.entity.Post;
import com.thinkbigdata.clevo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost(Post post);

    Page<Comment> findByUser(User user, Pageable page);
    List<Comment> findByUser(User user);
}
