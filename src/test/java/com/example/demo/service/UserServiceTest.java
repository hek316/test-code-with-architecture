package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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

    @MockBean
    private JavaMailSender javaMailSender;

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

    @Test
    void UserCreateDto_를_이용하여_유저를_생성할_수있다() {
        // given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("yhr05008@naver.com")
                .nickname("ek")
                .address("Seoul")
                .build();

        // Mock 객체 설정: 실제 이메일 발송을 막고 테스트만 수행
        // javaMailSender 는  Mock 객체로 대체되어 실제 send()호출 시 아무 일도 일어나지 않음
        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // when
        UserEntity userEntity = userService.create(userCreateDto);
        // then
        Assertions.assertThat(userEntity.getId()).isNotNull();
        Assertions.assertThat(userEntity.getStatus()).isEqualTo(UserStatus.PENDING);
    }

    @Test
    void UserUpdateDto_를_이용하여_유저를_수정할_수있다() {
        // given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname("ek2")
                .address("Seoul2")
                .build();

        // when
        userService.update(1L, userUpdateDto);
        // then

        UserEntity userEntity =  userService.getById(1L);
        Assertions.assertThat(userEntity.getNickname()).isEqualTo("ek2");
        Assertions.assertThat(userEntity.getAddress()).isEqualTo("Seoul2");
    }

    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() {
        // given
        // when
        userService.login(1L);

        // then
        UserEntity userEntity =  userService.getById(1L);
        Assertions.assertThat(userEntity.getLastLoginAt()).isGreaterThan(0L);
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다() {
        // given
        // when
        userService.verifyEmail(2L,"aaa-bb");

        // then
        UserEntity userEntity =  userService.getById(1L);
        Assertions.assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러를_던진다() {
        // given
        // when
        // then
        Assertions.assertThatThrownBy(() -> userService.verifyEmail(2L,"aaa-bb-test"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }


}