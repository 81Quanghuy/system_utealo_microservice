package vn.iostar.mediaservice.exception.wrapper;

public class MyFeignException extends RuntimeException{

    public MyFeignException() {
        super();
    }

    public MyFeignException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MyFeignException(final String message) {
        super(message);
    }

    public MyFeignException(final Throwable cause) {
        super(cause);
    }
}
