package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
class UserRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void UserRepository_가_제대로_연결되었다() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setNickname("test");

        // when
        UserEntity findUser = userJpaRepository.save(userEntity);

        // then
        assertThat(findUser.getNickname()).isEqualTo("test");
    }

    @Test
    void  findByIdAndStatus_로_유저_데이터를_찾아올_수_있다() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setNickname("test");
        userEntity.setStatus(UserStatus.PENDING);
        UserEntity findUser = userJpaRepository.save(userEntity);

        // when
        Optional<UserEntity> byIdAndStatus = userJpaRepository.findByIdAndStatus(findUser.getId(), UserStatus.PENDING);

        // then
        Assertions.assertThat(byIdAndStatus.isPresent()).isTrue();
    }

    @Test
    void  findByIdAndStatus_는_데이터가_없으면_Optional_empty_를_내려준다() {
        // given
        // when
        Optional<UserEntity> byIdAndStatus = userJpaRepository.findByIdAndStatus(2L, UserStatus.PENDING);

        // then
        Assertions.assertThat(byIdAndStatus.isEmpty()).isTrue();
    }

    @Test
    void  ffindByEmailAndStatus_로_유저_데이터를_찾아올_수_있다() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setNickname("test");
        userEntity.setStatus(UserStatus.PENDING);
        userEntity.setEmail("test");
        UserEntity findUser = userJpaRepository.save(userEntity);

        // when
        Optional<UserEntity> byIdAndStatus = userJpaRepository.findByEmailAndStatus(findUser.getEmail(), UserStatus.PENDING);

        // then
        Assertions.assertThat(byIdAndStatus.isPresent()).isTrue();
    }

    @Test
    void  findByEmailAndStatus_데이터가_없으면_Optional_empty_를_내려준다() {
        // given
        // when
        Optional<UserEntity> byIdAndStatus = userJpaRepository.findByEmailAndStatus("test", UserStatus.PENDING);

        // then
        Assertions.assertThat(byIdAndStatus.isEmpty()).isTrue();
    }


}