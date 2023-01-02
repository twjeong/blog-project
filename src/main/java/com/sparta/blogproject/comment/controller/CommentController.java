package com.sparta.blogproject.comment.controller;

import com.sparta.blogproject.comment.dto.CommentRequestDto;
import com.sparta.blogproject.comment.dto.CommentResponseDto;
import com.sparta.blogproject.comment.service.CommentService;
import com.sparta.blogproject.common.security.UserDetailsImpl;
import com.sparta.blogproject.post.dto.PostRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts/comments")
public class CommentController {
    private final CommentService commentService;

//    작성
    @PostMapping("/")
    public ResponseEntity createComment(@RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.createComment(commentRequestDto, userDetails.getUser());
        return ResponseEntity.ok("작성 완료");
    }

//    수정
    @PutMapping("/{id}")
    public ResponseEntity updateComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.updateComment(id, commentRequestDto, userDetails.getUser());
        return ResponseEntity.ok("수정 완료");
    }

//    삭제
    @DeleteMapping("/{id}")
    public ResponseEntity deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(id, userDetails.getUser());
        return ResponseEntity.ok("삭제 완료");
    }

}
