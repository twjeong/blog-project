package com.sparta.blogproject.user.service;

import com.sparta.blogproject.common.jwt.JwtUtil;
import com.sparta.blogproject.user.dto.LoginRequest;
import com.sparta.blogproject.user.dto.ResponseStatusDto;
import com.sparta.blogproject.user.dto.SignupRequest;
import com.sparta.blogproject.user.dto.StatusEnum;
import com.sparta.blogproject.user.entity.User;
import com.sparta.blogproject.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Spy
    private JwtUtil jwtUtil;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입")
    void signup() {
        // given
        SignupRequest request = SignupRequest.builder()
                .admin(false)
                .username("nathan")
                .password("1234qwer")
                .email("nathan@gmail.com")
                .build();

        when(userRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.empty());

        // when
        ResponseStatusDto response = userService.signup(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(StatusEnum.SIGN_SUCCESS.getStatusCode());
        assertThat(response.getMsg()).isEqualTo(StatusEnum.SIGN_SUCCESS.getMsg());
    }

    @Test
    @DisplayName("로그인")
    void login() {
        // given
        LoginRequest request = LoginRequest.builder()
                .username("nathan")
                .password("1234qwer")
                .build();

        User user = new User("nathan", passwordEncoder.encode("1234qwer"), null, null);

        when(userRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.of(user));

        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        // when
        ResponseStatusDto response = userService.login(request, servletResponse);

        // then
        assertThat(response.getStatusCode()).isEqualTo(StatusEnum.LOGIN_SUCCESS.getStatusCode());
        assertThat(response.getMsg()).isEqualTo(StatusEnum.LOGIN_SUCCESS.getMsg());
        assertThat(servletResponse.getHeaderValue("Authorization").toString()).isNotEmpty();

        verify(userRepository).saveAndFlush(any(User.class));
    }
}