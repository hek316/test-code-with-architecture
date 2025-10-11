package com.example.demo.controller;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        // 테스트 종류 후 데이터 정리
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    // HTTP 요청/응답을 시뮬레이션할 객체
    // 컨트롤러 단위 테스트 또는 통합 테스트 시 사용

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    // Java 객체를 JSON 문자열로 변환(직렬화) 하거나 그 반대로 변환(역직렬화)하는 도구


    @Test
    void 사용자는_특정_유저의_정보를_개인정보는_소건된_채_전달_받을_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("yhr05008@naver.com"))
                .andExpect(jsonPath("$.nickname").value("ek"))
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

    }

    @Test
    void 사용자는_존재하지_않은_유저의_아이디로_api_호출할_경우_404응답을_받는다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/888"))
                .andExpect(status().isNotFound())
                            .andExpect(content().string("Users에서 ID 888를 찾을 수 없습니다."));
    }

    @Test
    void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaa-bb"))
                .andExpect(status().isFound());


        UserEntity userEntity = userRepository.findById(2L).get();
        Assertions.assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는_인증_코드거_일치하지_않을_경우_권한없음_에러를_내려준다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaa-bbcccc"))
                .andExpect(status().isForbidden());

    }


    @Test
    void 사용자는_내_정보를_불러올_때_개인정보인_주소도_갖고_올_수_있다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/users/me")
                        .header("EMAIL", "yhr05008@naver.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("seoul"))
                .andExpect(jsonPath("$.nickname").value("ek"))
                .andExpect(jsonPath("$.email").value("yhr05008@naver.com"));

    }

    @Test
    void 사용자는_내_정보를_수정할_수_있다() throws Exception {
        // given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("seoul-2")
                .nickname("ek-2")
                .build();
        // when
        // then
        mockMvc.perform(put("/api/users/me")
                        .header("EMAIL", "yhr05008@naver.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.address").value("seoul-2"))
                .andExpect(jsonPath("$.nickname").value("ek-2"))
                .andExpect(jsonPath("$.email").value("yhr05008@naver.com"));

    }


}