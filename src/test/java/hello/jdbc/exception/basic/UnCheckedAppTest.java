package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unchecked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(()->controller.request()).isInstanceOf(RuntimeException.class);
    }

    @Test
    void printEx(){
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e){
            log.info("ex", e);
        }
    }

    static class Controller{
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service{
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
            /**
             * 둘다 이제 언체크 예외임
             */
        }
    }

    static class NetworkClient{
        public void call() {
            throw new RuntimeConnectException("connect FAIL");
        }
    }

    static class Repository{
        public void call() {
            try {
                runSQL();
            }catch (SQLException e){
                throw new RuntimeSQLException(e);
                /**
                 * SQLException 오게 되면 RuntimeException으로 던짐
                 */
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    /**
     * RuntimeException 즉 언체크 예외 생성
     */
    static class RuntimeConnectException extends RuntimeException{
        public RuntimeConnectException(String message){
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException() {
        }

        public RuntimeSQLException(Throwable cause){
            super(cause);
        }
    }
}
