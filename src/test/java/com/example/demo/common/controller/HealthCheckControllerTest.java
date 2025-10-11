package com.example.demo.common.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;
    // HTTP 요청/응답을 시뮬레이션할 객체
    // 컨트롤러 단위 테스트 또는 통합 테스트 시 사용


    @Test
    void 헬스_체크_응답이_200으로_내려온다() throws Exception {
        // /health_check.html로 GET 요청을 보내고
        // HTTP 상태 코드가 200(OK)인지 검증
        mockMvc.perform(get("/health_check.html")).andExpect(status().isOk());
    }


}