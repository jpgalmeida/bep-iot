package example.com.lockr.exceptions;

@SuppressWarnings({"squid:UndocumentedApi", "squid:S2166"})
public class ForbiddenException extends ServiceException {
	private static final long serialVersionUID = 1L;

    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
