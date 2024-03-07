package vn.iostar.emailservice.exception.wrapper;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
