package com.example.demo.user.controller;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        // 테스트 종류 후 데이터 정리
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserCreateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    // HTTP 요청/응답을 시뮬레이션할 객체
    // 컨트롤러 단위 테스트 또는 통합 테스트 시 사용

    @MockBean
    private JavaMailSender javaMailSender;

    private final ObjectMapper objectMapper = new ObjectMapper();



    @Test
    void 사용자는_회원가입을_할_수_있고_회원가입된_사용자는_PENDING_상태이다() throws Exception {
        // given
        UserCreate userCreate = UserCreate.builder()
                .email("yhr05008@naver.com")
                .address("seoul-2")
                .nickname("ek-2")
                .build();

        // when
        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));


        // then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nickname").value("ek-2"))
                .andExpect(jsonPath("$.email").value("yhr05008@naver.com"))
                .andExpect(jsonPath("$.status").value(UserStatus.PENDING.toString()));

    }


}