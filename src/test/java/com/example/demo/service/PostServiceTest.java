package com.example.demo.service;

import com.example.demo.model.dto.PostCreateDto;
import com.example.demo.model.dto.PostUpdateDto;
import com.example.demo.repository.PostEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest
@SqlGroup({

        // 각 테스트 실행 전에 더미 데이터 삽입
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        // 테스트 종류 후 데이터 정리
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void getPostById는_존재하는_게시물을_내려준다() {

        // given
        // when
        PostEntity postEntity = postService.getById(1L);
        // then
        Assertions.assertThat(postEntity.getContent()).isEqualTo("hello");
        Assertions.assertThat(postEntity.getWriter().getEmail()).isEqualTo("yhr05008@naver.com");

    }

    @Test
    void PostCreateDto_를_이용하여_게시물을_생성할_수있다() {
        // given
        PostCreateDto postCreateDto = PostCreateDto.builder()
                .content("test-content")
                .writerId(1L)
                .build();


        // when
        PostEntity postEntity = postService.create(postCreateDto);
        // then
        Assertions.assertThat(postEntity.getId()).isNotNull();
        Assertions.assertThat(postEntity.getContent()).isEqualTo("test-content");
        Assertions.assertThat(postEntity.getCreatedAt()).isGreaterThan(0);
    }

    @Test
    void PostUpdateDto_를_이용하여_게시물을_수정할_수있다() {
        // given
        PostUpdateDto postUpdateDto = PostUpdateDto.builder()
                .content("test-content-2")
                .build();

        // when
        postService.update(1L, postUpdateDto);
        // then
        PostEntity postEntity =  postService.getById(1L);
        Assertions.assertThat(postEntity.getContent()).isEqualTo("test-content-2");
        Assertions.assertThat(postEntity.getModifiedAt()).isGreaterThan(0);
    }

}