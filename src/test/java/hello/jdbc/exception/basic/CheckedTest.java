package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checked_throw(){
        Service service = new Service();
        assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyCheckedException.class);
        //callThrow의 경우 던지고 있기 때문에 여기서 또 잡거나 던져야 함
        //하지만 잡아주면 최종 단계이기 때문에 예외를 컴파일 단계에서 뱉어내게 됨
    }

    /*
    Exception을 상속받은 예외는 체크 예외가 된다.
     */

    static class MyCheckedException extends Exception{
        public MyCheckedException(String message){
            super(message);
        }
    }

    static class Service{
        Repository repository = new Repository();
        /*
        예외를 잡아서 처리 -> 예외를 잡아서 처리 혹은 던져야 함
         */
        public void callCatch(){
            try {
                repository.call();
//              call의 경우 체크예외를 던지고 있기 때문에 잡아주거나 던져야 함
            } catch (MyCheckedException e) {
                //예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
                //exception은 그냥 출력되게 된다.(e)
            }
        }
        
        /*
        체크예외를 밖으로 던지는 코드
         */
        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository{
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
//          위에서 정의한 MyCheckedException 발생 -> 체크예외이기 때문에 던지거나 처리해야 함
        }
    }
}
