package com.sparta.blogproject.user.controller;

import com.google.gson.Gson;
import com.sparta.blogproject.user.dto.LoginRequest;
import com.sparta.blogproject.user.dto.ResponseStatusDto;
import com.sparta.blogproject.user.dto.SignupRequest;
import com.sparta.blogproject.user.dto.StatusEnum;
import com.sparta.blogproject.user.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("회원가입 (성공)")
    void signup_success() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .admin(false)
                .username("nathan")
                .password("1234qwer")
                .email("nathan@gmail.com")
                .build();

        ResponseStatusDto response = new ResponseStatusDto(StatusEnum.SIGN_SUCCESS);

        when(userService.signup(any(SignupRequest.class)))
                .thenReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("statusCode", response.getStatusCode()).exists())
                .andExpect(jsonPath("msg", response.getMsg()).exists());
    }

    @Test
    @DisplayName("회원가입 (실패) - 아이디")
    void signup_failed_id() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .admin(false)
                .username("nat")
                .password("1234qwer")
                .email("nathan@gmail.com")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 (실패) - 비밀번호")
    void signup_failed_pw() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .admin(false)
                .username("nathan")
                .password("1234qwe")
                .email("nathan@gmail.com")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .username("nathan")
                .password("1234qwer")
                .build();

        ResponseStatusDto response = new ResponseStatusDto(StatusEnum.LOGIN_SUCCESS);

        when(userService.login(any(LoginRequest.class), any(HttpServletResponse.class)))
                .thenReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(request))
        );

        // then
        resultActions.andExpect(status().isOk());

        // verify
        verify(userService)
                .login(any(LoginRequest.class), any(HttpServletResponse.class));

    }

}