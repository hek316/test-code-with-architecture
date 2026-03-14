package com.example.demo.controller;

import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.PostUpdateDto;
import com.example.demo.repository.PostEntity;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;


    private final ObjectMapper objectMapper = new ObjectMapper();

    private long postId;
    private long activeUserId;

    @BeforeEach
    void setUp() {

        UserEntity userEntity = new UserEntity();
        userEntity.setNickname("test");
        userEntity.setEmail("test@test.com");
        userEntity.setAddress("Seoul");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setCertificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        activeUserId = userRepository.save(userEntity).getId();

        PostEntity postEntity = new PostEntity();
        postEntity.setContent("content");
        postEntity.setWriter(userEntity);
        postId = postRepository.save(postEntity).getId();
    }

    @Test
    void 사용자는_게시물을_단건_조회할_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.writer.nickname").value("test"));

    }

    @Test
    void 사용자는_존재하지_않는_게시물을_조회할_경우_에러가_난다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/" + 9999))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Posts에서 ID 9999를 찾을 수 없습니다."));

    }


    @Test
    void 사용자는_게시물을_수정할_수_있다() throws Exception {
        // given
        PostUpdateDto postUpdateDto = PostUpdateDto.builder()
                .content("updated content")
                .build();

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.content").value("updated content"))
                .andExpect(jsonPath("$.writer.nickname").value("test"));

    }

}