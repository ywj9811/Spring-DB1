package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

//트랜잭션 매니저 사용

@Slf4j
public class MemberServiceV3_2 {

//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
//        PlatformTransactionManager를 내부에서 주입 받아서 사용하도록 할 것이다.
//        이를 통해서 좀 더 유연함을 가져갈 수 있다.
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status) -> {
            //비즈니스 로직 처리
            try {
                bizLogic(toId, money, fromId);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
        //이를 통해서 commit 혹은 rollback 의 과정을 모두 완료해줌
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
