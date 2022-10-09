package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

//트랜잭션 -@Transaction AOP

@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional //이 메소드가 시작할 때 트랜잭션을 시행하겠다. (try catch 모두 시행 알아서)
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //비즈니스 로직 처리
        bizLogic(toId, money, fromId);
    }

    private void bizLogic(String toId, int money, String fromId) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        //오류 케이스 하나 추가
        validation(toMember);
        //
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        //오류 케이스 검증
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
