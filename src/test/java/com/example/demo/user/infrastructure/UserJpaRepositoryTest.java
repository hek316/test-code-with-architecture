package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

// 학습용 테스트: H2 인메모리 DB와 JPA 연동 동작을 확인하기 위한 예제
@ExtendWith(SpringExtension.class) // 스프링 빈 등록, 의존성 주입등을 지원(생략 가능)
@DataJpaTest(showSql = true) // jpa 관련 컴포넌트 entity, repository만 로드해서 테스트, 실제 DB 대신 기본적인 H2(인메모리 DB)를 사용한다.
@Sql("/sql/user-repository-test-data.sql")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;


    @Test
    void findByIdAndStatus_로_유저_데이터를_찾아올_수_있다() {
        // given
       // when
        Optional<UserEntity> byIdAndStatus = userJpaRepository.findByIdAndStatus(1L, UserStatus.ACTIVE);

        // then
        Assertions.assertThat(byIdAndStatus.isPresent()).isTrue();
    }

    @Test
    void findByIdAndStatus_는_데이터가_없으면_Optional_empt를_내려준다() {
        // given
        // when
        Optional<UserEntity> byIdAndStatus = userJpaRepository.findByIdAndStatus(1L, UserStatus.PENDING);

        // then
        Assertions.assertThat(byIdAndStatus.isEmpty()).isTrue();
    }

    @Test
    void findByEmailAndStatus_로_유저_데이터를_찾아올_수_있다() {
        // given
        // when
        Optional<UserEntity> byEmailAndStatus = userJpaRepository.findByEmailAndStatus("yhr05008@naver.com", UserStatus.ACTIVE);

        // then
        Assertions.assertThat(byEmailAndStatus.isPresent()).isTrue();
    }

    @Test
    void findByEmailAndStatus_는_데이터가_없으면_Optional_empt를_내려준다() {
        // given
        // when
        Optional<UserEntity> byEmailAndStatus = userJpaRepository.findByEmailAndStatus("yhr05008@naver.com", UserStatus.ACTIVE);

        // then
        Assertions.assertThat(byEmailAndStatus.isEmpty()).isFalse();
    }
}