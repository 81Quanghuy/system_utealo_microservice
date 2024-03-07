package vn.iostar.emailservice.exception.wrapper;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message) {
        super(message);
    }
}
