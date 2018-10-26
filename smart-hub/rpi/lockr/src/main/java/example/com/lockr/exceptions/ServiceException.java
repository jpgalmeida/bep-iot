package example.com.lockr.exceptions;

/**
 * Created by shaun on 13.01.17.
 */
public class ServiceException extends RuntimeException {
    private final static long serialVersionUID = 1L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
