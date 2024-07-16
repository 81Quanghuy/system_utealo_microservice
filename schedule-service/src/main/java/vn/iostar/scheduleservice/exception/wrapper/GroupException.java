package vn.iostar.scheduleservice.exception.wrapper;

public class GroupException extends RuntimeException {

        public GroupException() {
            super();
        }

        public GroupException(final String message, final Throwable cause) {
            super(message, cause);
        }

        public GroupException(final String message) {
            super(message);
        }

        public GroupException(final Throwable cause) {
            super(cause);
        }
}
