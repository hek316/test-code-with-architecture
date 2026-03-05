package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private long activeUserId;
    private long pendingUserId;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setNickname("test");
        userEntity.setEmail("test@test.com");
        userEntity.setStatus(UserStatus.ACTIVE);
        activeUserId = userRepository.save(userEntity).getId();

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setNickname("test2");
        userEntity2.setEmail("test2@test.com");
        userEntity2.setStatus(UserStatus.PENDING);
        pendingUserId = userRepository.save(userEntity2).getId();
    }

    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        String email = "test@test.com";

        // when
        UserEntity findMember = userService.getByEmail(email);

        // then
        assertThat(findMember.getNickname()).isEqualTo("test");
    }

    @Test
    void getByEmail은_PEDING_상태인_유저를_찾아올_수_없다() {
        // given
        String email = "test2@test.com";

        // when
        // then
        assertThatThrownBy(() ->
                userService.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class);
    }



    @Test
    void getById는_ACTIVE_상태인_유저를_찾아올_수_있다() {
        // given
        // when
        UserEntity findMember = userService.getById(activeUserId);

        // then
        assertThat(findMember.getNickname()).isEqualTo("test");
    }

    @Test
    void getById는_PEDING_상태인_유저를_찾아올_수_없다() {
        // given
        // when
        // then
        assertThatThrownBy(() ->
                userService.getById(pendingUserId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}