package com.sparta.blogproject.post.service;

import com.sparta.blogproject.post.dto.PostRequestDto;
import com.sparta.blogproject.post.dto.PostResponseDto;
import com.sparta.blogproject.post.entity.Post;
import com.sparta.blogproject.post.repository.PostRepository;
import com.sparta.blogproject.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void createPost() {

        // given
        PostRequestDto request = new PostRequestDto();
        User user = new User();

        // when
        postService.createPost(request, user);

        // then
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getPosts() {
        // given
        Pageable pageable = Pageable.ofSize(20);
        when(postRepository.findAll(pageable)).thenReturn(Page.empty());

        // when
        Page<PostResponseDto> response = postService.getPosts(pageable);

        // then
        assertThat(response).isEmpty();

        // verify
        verify(postRepository).findAll(pageable);
    }

    @Test
    @DisplayName("게시글 id가 없을때 예외 발생")
    void getPostById_throw() {
        // given
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            postService.getPostById(0L);
        });
    }

    @Test
    @DisplayName("게시글 가져오기")
    void getPostById() {
        // given
        PostRequestDto request = PostRequestDto.builder()
                .contents("hello.. hello..")
                .title("hello")
                .build();

        Post post = new Post(request, new User());
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when
        PostResponseDto response = postService.getPostById(anyLong());

        // then
        assertThat(response.getContents()).isEqualTo(request.getContents());
        assertThat(response.getTitle()).isEqualTo(request.getTitle());

        // verify
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("게시글 업데이트")
    void updatePost() {
        // given
        PostRequestDto request = PostRequestDto.builder()
                .contents("hello.. hello..")
                .title("hello")
                .build();
        User user = new User("nathan", "1234qwer", null, null);
        Post post = new Post(request, user);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when
        postService.updatePost(anyLong(), request, user);

        // verify
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 업데이트 (다른 사람이 작성하려할때)")
    void updatePost_invalid_user() {
        // given
        PostRequestDto request = PostRequestDto.builder()
                .contents("hello.. hello..")
                .title("hello")
                .build();
        User userInPost = new User("nathan", "1234qwer", null, null);
        User userInput = new User("joel", "1234qwer", null, null);
        Post post = new Post(request, userInPost);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> postService.updatePost(anyLong(), request, userInput));
    }
}