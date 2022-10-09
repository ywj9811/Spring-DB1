package hello.jdbc.repository;

//JDBC - DriverManager 사용해보기
//옛날에 사용하던 방식이다 보니 현재는 사용하지 않는 방식임

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            //connection 획득
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate();
//          update몇개 했는지 반환함 executeUpdate
            return member;

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
//          완료하고 나면 resource를 모두 close하도록 한다(무조건 할 수 있도록 finally에서)
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if (rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId="+ memberId);
            }

        } catch (SQLException e) {
            log.error("error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if (stmt != null){
            try {
                stmt.close(); //만약 Exception이 발생하면 catch로 잡을 수 있기 때문에 connection 또한 close가 가능
            } catch (SQLException e) {
                log.error("error", e);
            }

        }

        if (con != null){
            try {
                con.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            //connection 획득
            pstmt = con.prepareStatement(sql);
            pstmt.setString(2, memberId);
            pstmt.setInt(1, money);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
//          완료하고 나면 resource를 모두 close하도록 한다(무조건 할 수 있도록 finally에서)
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            //connection 획득
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
//          완료하고 나면 resource를 모두 close하도록 한다(무조건 할 수 있도록 finally에서)
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
//    connection 가져오는 메소드
}
