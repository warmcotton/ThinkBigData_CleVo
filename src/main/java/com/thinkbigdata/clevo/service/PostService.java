package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.dto.post.CommentDto;
import com.thinkbigdata.clevo.dto.post.PostDto;
import com.thinkbigdata.clevo.entity.Comment;
import com.thinkbigdata.clevo.entity.Post;
import com.thinkbigdata.clevo.entity.User;
import com.thinkbigdata.clevo.repository.CommentRepository;
import com.thinkbigdata.clevo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final BasicEntityService basicEntityService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    public PostDto addPost(String email, PostDto postDto) {
        User user = basicEntityService.getUserByEmail(email);

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setUser(user);
        post.setViews(0);

        postRepository.save(post);

        return basicEntityService.getPostDto(post, new ArrayList<>());
    }

    public PostDto getPost(Integer id) {
        Post post = basicEntityService.getPost(id);
        post.setViews(post.getViews()+1);
        List<Comment> comments = commentRepository.findByPost(post);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(basicEntityService.getCommentDto(comment));
        }
        return basicEntityService.getPostDto(post, commentDtos);
    }

    public Page<PostDto> getAllPosts(String search, Pageable page) {
        return postRepository.findByTitleContainingOrContentContaining(search, search, page)
                .map(post -> PostDto.builder().id(post.getId()).title(post.getTitle()).writer(post.getUser().getEmail())
                        .content(post.getContent()).views(post.getViews()).created(post.getCreated()).modified(post.getModified()).build());
    }

    public PostDto addComment(String email, Integer postId, String content) {
        User user = basicEntityService.getUserByEmail(email);
        Post post = basicEntityService.getPost(postId);

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findByPost(post);
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment c : comments) {
            commentDtos.add(basicEntityService.getCommentDto(c));
        }
        return basicEntityService.getPostDto(post, commentDtos);
    }

    public void deletePost(String email, Integer postId) {
        User user = basicEntityService.getUserByEmail(email);
        Post post = basicEntityService.getPost(postId);

        if (post.getUser() != user) throw new AccessDeniedException("해당 게시글에 대한 삭제 권한이 없습니다.");

        List<Comment> comments = commentRepository.findByPost(post);
        commentRepository.deleteAll(comments);

        postRepository.delete(post);
    }

    public void deleteComment(String email, Integer commentId) {
        User user = basicEntityService.getUserByEmail(email);
        Comment comment = basicEntityService.getComment(commentId);

        if (comment.getUser() != user) throw new AccessDeniedException("해당 댓글에 대한 삭제 권한이 없습니다.");

        commentRepository.delete(comment);
    }

    public Page<PostDto> getUserPostDtos(String email, Pageable page) {
        User user = basicEntityService.getUserByEmail(email);
        return postRepository.findByUser(user, page).map(post -> PostDto.builder().id(post.getId()).title(post.getTitle()).writer(post.getUser().getEmail())
                .content(post.getContent()).views(post.getViews()).created(post.getCreated()).modified(post.getModified()).build());
    }

    public Page<CommentDto> getUserCommentsDtos(String email, Pageable page) {
        User user = basicEntityService.getUserByEmail(email);
        return commentRepository.findByUser(user, page).map(comment -> CommentDto.builder().id(comment.getId()).post_id(comment.getPost().getId()).writer(comment.getUser().getEmail())
                .content(comment.getContent()).date(comment.getCreated()).build());
    }

    public List<Post> getUserPost(String email) {
        User user = basicEntityService.getUserByEmail(email);
        return postRepository.findByUser(user);
    }

    public List<Comment> getUserComments(String email) {
        User user = basicEntityService.getUserByEmail(email);
        return commentRepository.findByUser(user);
    }
}
