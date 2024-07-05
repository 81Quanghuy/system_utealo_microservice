package vn.iostar.scheduleservice.dto.response;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
