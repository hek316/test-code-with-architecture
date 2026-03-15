package com.example.demo.post.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.infrastructure.PostEntity;
import com.example.demo.post.infrastructure.PostJpaRepository;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    private long activeUserId;
    private long savedPostId;

    @BeforeEach
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setNickname("test");
        userEntity.setEmail("test@test.com");
        userEntity.setStatus(UserStatus.ACTIVE);
        activeUserId = userJpaRepository.save(userEntity).getId();

        PostEntity postEntity = new PostEntity();
        postEntity.setContent("테스트 게시물");
        postEntity.setWriter(userEntity);
        postEntity.setCreatedAt(1678530673958L);
        savedPostId = postJpaRepository.save(postEntity).getId();
    }

    @Test
    void getPostById는_존재하는_게시물을_내려준다() {
        // given
        // when
        PostEntity result = postService.getPostById(savedPostId);

        // then
        assertThat(result.getContent()).isEqualTo("테스트 게시물");
        assertThat(result.getWriter().getNickname()).isEqualTo("test");
    }

    @Test
    void getPostById는_존재하지_않는_게시물이면_에러를_던진다() {
        // given
        // when
        // then
        assertThatThrownBy(() ->
                postService.getPostById(999999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void PostCreateDto를_이용하여_게시물을_생성할_수_있다() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(activeUserId)
                .content("새 게시물")
                .build();

        // when
        PostEntity savedPost = postService.create(postCreate);

        // then
        assertThat(savedPost.getId()).isNotNull();
        assertThat(savedPost.getContent()).isEqualTo("새 게시물");
        assertThat(savedPost.getWriter().getNickname()).isEqualTo("test");
        assertThat(savedPost.getCreatedAt()).isGreaterThan(0);
    }

    @Test
    void PostUpdateDto를_이용하여_게시물을_수정할_수_있다() {
        // given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("수정된 게시물")
                .build();

        // when
        postService.update(savedPostId, postUpdate);

        // then
        PostEntity updatedPost = postService.getPostById(savedPostId);
        assertThat(updatedPost.getContent()).isEqualTo("수정된 게시물");
        assertThat(updatedPost.getModifiedAt()).isGreaterThan(0);
    }

}
