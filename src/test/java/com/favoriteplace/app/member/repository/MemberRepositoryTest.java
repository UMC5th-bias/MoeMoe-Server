package com.favoriteplace.app.member.repository;

import com.favoriteplace.app.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private static Member member;

    @BeforeEach
    void setUser() {
        member = Member.create(
                "yoon",
                "sss@naver.com",
                true,
                "자기소개",
                "image",
                null);
    }

    @Test
    @DisplayName("동일한 이메일로 사용자가 동시에 가입할 경우, 하나의 계정만 생성된다")
    void joinMemberWithConcurrent() throws InterruptedException {
        //given
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        //when
        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                try {
                    memberRepository.save(member);
                } catch (DataIntegrityViolationException e) {
                    exceptions.add(e);
                }
                latch.countDown();
            });
        }
        latch.await();

        //then
        List<Member> savedMember = memberRepository.findAllByEmail(member.getEmail());
        assertAll(
                () -> assertThat(exceptions).hasSize(2),
                () -> assertThat(savedMember).hasSize(1)
        );
    }

}