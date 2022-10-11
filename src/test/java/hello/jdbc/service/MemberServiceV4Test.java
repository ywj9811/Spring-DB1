package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import hello.jdbc.repository.MemberRepositoryV4_1;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 예외 누수 문제 해결
 * SQLException 해결'
 *
 * MemberRepository 인터페이스 의존
 */

@Slf4j
@SpringBootTest //이 어노테이션은 스프링을 하나 생성하면서 자동으로 빈을 등록해준다
class MemberServiceV4Test {

    public static final String Member_A = "memberA";
    public static final String Member_B = "memberB";
    public static final String Member_EX = "ex";

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberServiceV4 memberService;

//  스프링 빈 등록 -> 트랜잭션 aop는 빈들이 등록되어 있어야 사용할 수 있다.
    @TestConfiguration
    static class TestConfig{

        private final DataSource dataSource;

        TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }
        @Bean
        MemberRepository memberRepository(){
            return new MemberRepositoryV4_1(dataSource);
            //인터페이스를 제대로 된 구현체로
        }

        @Bean
        MemberServiceV4 memberServiceV4(){
            return new MemberServiceV4(memberRepository());
            //인터페이스를 구현한 memberRepositoryV4_1를 넣어서 MemberServiceV4 구현
        }
    }

    @AfterEach
    void after() {
        memberRepository.delete(Member_A);
        memberRepository.delete(Member_EX);
        memberRepository.delete(Member_B);
    }

    @Test
    void AopCheck(){
        log.info("memberService={}", memberService.getClass());
//      해당 서비스 로직은 프록시가 자동으로 상속받아 생성한 같은 로직의 다른 클래스이다.
        log.info("memberRepository={}", memberRepository.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberRepository));
        Assertions.assertThat(AopUtils.isAopProxy(memberService));
    }

    @Test
    @DisplayName("success")
    void accountTransfer() {
        //given
        Member memberA = new Member(Member_A, 10000);
        Member memberB = new Member(Member_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        log.info("START TX");
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 5000);
        log.info("END TX");

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(5000);
        assertThat(findMemberB.getMoney()).isEqualTo(15000);
    }

    @Test
    @DisplayName("fail")
    void accountTransferEx() {
        //given
        Member memberA = new Member(Member_A, 10000);
        Member memberEX = new Member(Member_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEX);

        //when
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), 5000))
                .isInstanceOf(IllegalStateException.class);

        //then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEX = memberRepository.findById(memberEX.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        //rollback 이 되어 10000원으로 돌아온 것이다.
        assertThat(findMemberEX.getMoney()).isEqualTo(10000);
    }
}