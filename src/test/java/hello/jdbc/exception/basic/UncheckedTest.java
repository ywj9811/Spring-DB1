package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedTest {

    @Test
    void unchecked_catch(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unchecked_throw(){
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow()).isInstanceOf(MyUncheckedException.class);
        //해당하는 예외가 발생했따.
    }

    /**
     * RuntimeException을 상속받는 예외는 언체크 예외가 된다.
     */
    static class MyUncheckedException extends RuntimeException{
        public MyUncheckedException(String message){
            super(message);
        }
    }

    /**
     * UnChecked 예외는 잡거나, 던지지 않아도 됨
     * 잡지 않는다면 자동으로 던져줌
     */
    static class Service{
        Repository repository = new Repository();

        public void callCatch(){
            try {
                repository.call();
            } catch (MyUncheckedException e){
                //예외 처리 로직
                log.info("예외 처리, message={}", e.getMessage(), e);
            }
        }

        /**
         * 따로 던지지 않아도 자동으로 상위로 던져버림
         */
        public void callThrow(){
            repository.call();
        }
    }

    static class Repository{
        /**
         * RuntimeExceptioin을 발생시키지만, throws를 사용하지 않아도 됨(언체크)
         */
        public void call(){
            throw new MyUncheckedException("ex");
        }
    }
}
