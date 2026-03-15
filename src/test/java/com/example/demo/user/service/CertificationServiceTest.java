package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificationServiceTest {

    @Test
    public void 이메일과_컨텐츠가_제대로_만들어져_보내지는지_테스트한다() {
        // given
        FakeMailSender mailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(mailSender);
        String email = "test@test.com";
        long id = 2L;
        String certification = "test";

        // when
        certificationService.send(email, id, certification);

        // then
        Assertions.assertThat(mailSender.email).isEqualTo(email);
        Assertions.assertThat(mailSender.content).isEqualTo( "Please click the following link to certify your email address: http://localhost:8080/api/users/2/verify?certificationCode=test");
        Assertions.assertThat(mailSender.title).isEqualTo("Please certify your email address");

    }
}