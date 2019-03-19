package dictionary.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Supper class for all business logic exceptions
 */
@Getter
public class ServiceException extends RuntimeException {
	private final String code;
	private final HttpStatus httpStatus;

	public ServiceException(String message, String code, HttpStatus httpStatus) {
		super(message);
		this.code = code;
		this.httpStatus = httpStatus;
	}
}
