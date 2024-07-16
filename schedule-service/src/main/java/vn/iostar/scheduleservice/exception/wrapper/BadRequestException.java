package vn.iostar.scheduleservice.exception.wrapper;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
