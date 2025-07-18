package com.favoriteplace.app.member.service;

import static com.favoriteplace.global.exception.ErrorCode.NOT_VAILD_EMAIL_AUTHCODE;

import com.favoriteplace.app.member.controller.dto.MemberDto;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.RedisUtil;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailSendService {

    @Value("${mail.username}")
    private String host;

    private final JavaMailSender mailSender;
    private int authNumber;

    private final RedisUtil redisUtil;

    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber);
    }

    public MemberDto.EmailSendResDto joinEmail(String email) {
        makeRandomNumber();
        String setFrom = host; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "[최애의 장소] 회원 가입 인증 이메일 입니다.";
        String content =
                "최애의 장소를 방문해주셔서 감사합니다." +
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "인증번호를 올바르게 입력해주세요 :)";
        mailSend(setFrom, toMail, title, content);
        return new MemberDto.EmailSendResDto(authNumber);
    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        //JavaMailSender 객체를 사용하여 MimeMessage 객체를 생성
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom); //이메일의 발신자 주소 설정
            helper.setTo(toMail); //이메일의 수신자 주소 설정
            helper.setSubject(title); //이메일의 제목을 설정
            helper.setText(content, true); //이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(message);
        } catch (MessagingException e) { //이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            e.printStackTrace();
        }

        redisUtil.setDataExpire(Integer.toString(authNumber), toMail, 60 * 5L); // 제한시간 5분
    }

    public void checkAuthNum(String email, String authNum) {
        if (redisUtil.getData(authNum) == null) {
            throw new RestApiException(NOT_VAILD_EMAIL_AUTHCODE);
        }
    }
}