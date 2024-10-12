package com.thinkbigdata.clevo.controller;

import com.thinkbigdata.clevo.dto.post.PostDto;
import com.thinkbigdata.clevo.entity.Post;
import com.thinkbigdata.clevo.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/post/add")
    public ResponseEntity<PostDto> addPost(Authentication authentication, @RequestBody @Valid PostDto postDto) {
        return ResponseEntity.ok(postService.addPost(authentication.getName(), postDto));
    }

    @GetMapping("/post/{post_id}")
    public ResponseEntity<PostDto> getPost(@PathVariable("post_id") Integer postId) {
        if (postId == null || postId <= 0)
            throw new IllegalArgumentException("post_id 정보가 유효하지 않습니다.");

        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostDto>> getAllPost(@RequestParam(value = "search", defaultValue = "") String search, @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(search, pageable));
    }

    @PostMapping("/comment/add/{post_id}")
    public ResponseEntity<PostDto> addComment(Authentication authentication, @PathVariable("post_id") Integer postId, @RequestBody Map<String, String> comment) {
        if (postId == null || postId <= 0)
            throw new IllegalArgumentException("post_id 정보가 유효하지 않습니다.");
        if (!comment.containsKey("content")) throw new IllegalArgumentException("content 정보가 유효하지 않습니다.");
        if (comment.get("content") == null) throw new IllegalArgumentException("content 정보가 유효하지 않습니다.");

        return ResponseEntity.ok(postService.addComment(authentication.getName(), postId, comment.get("content")));
    }

    @DeleteMapping("/post/delete/{post_id}")
    public ResponseEntity<?> deletePost(Authentication authentication, @PathVariable("post_id") Integer postId) {
        postService.deletePost(authentication.getName(), postId);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/comment/delete/{comment_id}")
    public ResponseEntity<?> deleteComment(Authentication authentication, @PathVariable("comment_id") Integer commentId) {
        postService.deleteComment(authentication.getName(), commentId);
        return ResponseEntity.ok(null);
    }
}
