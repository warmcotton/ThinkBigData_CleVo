package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.Post;
import com.thinkbigdata.clevo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);


    Page<Post> findByUser(User user, Pageable page);
    List<Post> findByUser(User user);
}
