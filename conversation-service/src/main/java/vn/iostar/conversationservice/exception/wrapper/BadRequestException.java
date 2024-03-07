package vn.iostar.conversationservice.exception.wrapper;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
