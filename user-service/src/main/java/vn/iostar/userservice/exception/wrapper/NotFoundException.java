package vn.iotstart.userservice.exception.wrapper;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
