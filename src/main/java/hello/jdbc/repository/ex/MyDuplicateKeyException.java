package hello.jdbc.repository.ex;

/**
 * 데이터 중복의 경우 던지기 위한 클래스
 * 우리가 만든 예외이기 때문에 jdbc 혹은 jpa와 같은 기술에 따라서 영향을 받지 않는다.
 */

public class MyDuplicateKeyException extends MyDbException{

    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
