package com.sparta.blogproject.user.repository;

import com.sparta.blogproject.user.entity.User;
import com.sparta.blogproject.user.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자 추가")
    @Test
    void addUser() {
        // given
        User user = new User(
                "nathan",
                "1234qwer",
                "nathan@gmail.com",
                UserRoleEnum.USER
        );

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUser.getRole()).isEqualTo(user.getRole());
    }

    @DisplayName("사용자 조회")
    @Test
    void findByUsername() {
        // given
        User user = new User(
                "nathan",
                "1234qwer",
                "nathan@gmail.com",
                UserRoleEnum.USER
        );

        userRepository.save(user);

        // when
        Optional<User> savedUser = userRepository.findByUsername(user.getUsername());

        // then
        assertThat(savedUser).isPresent();
    }

}