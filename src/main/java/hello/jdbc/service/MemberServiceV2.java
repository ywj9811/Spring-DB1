package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//트랜젝션 사용 - 파라미터 연동, 풀을 고려한 종료

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false); //트랜젝션 시작
            //비즈니스 로직
            bizLogic(con, toId, money, fromId);
            con.commit(); //성공시 커밋
            
        } catch (Exception e){
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }

    }

    private void bizLogic(Connection con, String toId, int money, String fromId) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        //오류 케이스 하나 추가
        validation(toMember);
        //
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void release(Connection con) {
        if (con != null){
            try {
                con.setAutoCommit(true); //pool에 돌려주기 전에 true로 변경시켜 주는 것이다.
                con.close();
            } catch (Exception e){
                log.info("error", e);
                //예외 정보의 경우는 {} 필요 없다
            }
        }
    }

    private void validation(Member toMember) {
        //오류 케이스 검증
        if(toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
