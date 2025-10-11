package com.example.demo.controller;

import com.example.demo.post.domain.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
// 스프링 부트 전체 컨텍스트를 로딩
// 컨트롤러 , 서비스, 레파지토리 등 bean 을 모두 주입받아 테스트할 필요가 있을때
@AutoConfigureMockMvc
// MockMvc 객체를 스프링 컨텍스트에  자동 생성
// 실제 서버를 띄우지 않고 MVC 요청/응답을 시뮬레이션 가능
@AutoConfigureTestDatabase
// 테스트용 인메모리 DB(H2 등) 로 자동 설정
// 실제 DB에 영향없이 테스트 데이터 관리 가능

@SqlGroup({

        // 각 테스트 실행 전에 더미 데이터 삽입
        @Sql(value = "/sql/post-create-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        // 테스트 종류 후 데이터 정리
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    // HTTP 요청/응답을 시뮬레이션할 객체
    // 컨트롤러 단위 테스트 또는 통합 테스트 시 사용



    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void 사용자는_게시물을_작성할_수있다() throws Exception {
        // given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1L)
                .content("content-2")
                .build();

        // when
        // then
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.content").value("content-2"))
                .andExpect(jsonPath("$.writer.id").value(1L))
                .andExpect(jsonPath("$.writer.email").value("yhr05008@naver.com"))
                .andExpect(jsonPath("$.writer.nickname").value("ek"));

    }
}