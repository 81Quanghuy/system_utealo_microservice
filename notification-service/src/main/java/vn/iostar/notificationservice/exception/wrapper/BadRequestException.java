package vn.iostar.notificationservice.exception.wrapper;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
