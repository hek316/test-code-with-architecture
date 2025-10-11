package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.repository.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SqlGroup({

        // 각 테스트 실행 전에 더미 데이터 삽입
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        // 테스트 종류 후 데이터 정리
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void getByEmail은_ACTIVE_상태인_유저를_찾아올_수있다() {

        // given
        String email = "yhr05008@naver.com";
        // when
        UserEntity byEmail = userService.getByEmail(email);
        // then
        Assertions.assertThat(byEmail.getStatus()).isEqualTo(UserStatus.ACTIVE);
        Assertions.assertThat(byEmail.getNickname()).isEqualTo("ek");
    }

    @Test
    void getByEmail은_PENDING_상태인_유저는_찾아올_수없다() {

        // given
        String email = "test01@naver.com";
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getByEmail(email))
                        .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById는_ACTIVE_상태인_유저를_찾아올_수있다() {

        // given
        // when
        UserEntity userEntity = userService.getById(1);
        // then
        Assertions.assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        Assertions.assertThat(userEntity.getNickname()).isEqualTo("ek");
    }

    @Test
    void getById는_PENDING_상태인_유저는_찾아올_수없다() {

        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.getById(2))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}