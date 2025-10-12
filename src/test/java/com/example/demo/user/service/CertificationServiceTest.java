package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import com.example.demo.user.infrastructure.MailSenderImpl;
import com.example.demo.user.service.port.MailSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

class CertificationServiceTest {


    @Test
    void 이메일과_컨텐츠가_제대로_만들어져_보내지는지_테스트한다() {

        FakeMailSender mailSender = new FakeMailSender();
        CertificationService  certificationService = new CertificationService(mailSender);
        // given
        String email = "yhr05008@naver.com";
        long userId = 1L;
        String certificationCode = "aaa-aa";

        // when
        certificationService.send(email, userId, certificationCode);
        // then
        Assertions.assertThat(mailSender.email).isEqualTo(email);
        Assertions.assertThat(mailSender.title).isEqualTo("Please certify your email address");
        Assertions.assertThat(mailSender.content).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aaa-aa");
    }

}