package com.sparta.blogproject.user.service;

import com.sparta.blogproject.common.jwt.JwtUtil;
import com.sparta.blogproject.user.dto.LoginRequest;
import com.sparta.blogproject.user.dto.ResponseStatusDto;
import com.sparta.blogproject.user.dto.SignupRequest;
import com.sparta.blogproject.user.dto.StatusEnum;
import com.sparta.blogproject.user.entity.User;
import com.sparta.blogproject.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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
    private JwtUtil jwtUtil = new JwtUtil(username -> null);

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void prepare() {
        ReflectionTestUtils.setField(jwtUtil,
                "secretKey", // jwtUtil의 secretKey값이 저장될 변수
                "7ZWt7ZW0OTntmZTsnbTtjIXtlZzqta3snYTrhIjrqLjshLjqs4TroZzrgpjslYTqsIDsnpDtm4zrpa3tlZzqsJzrsJzsnpDrpbzrp4zrk6TslrTqsIDsnpA="); // secretKey의 값
        jwtUtil.init(); // jwtUtil에서 @PostConstructor가 동작하지 않기 때문에, 임의로 실행시켜야 함
    }

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