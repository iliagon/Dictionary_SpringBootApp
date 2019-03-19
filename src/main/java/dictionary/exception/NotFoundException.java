package dictionary.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Supper class for all 'NotFoundException'
 */
@Getter
public class NotFoundException extends ServiceException {
	private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
	private final String code;

	public NotFoundException(String message, String code) {
		super(message, code, HTTP_STATUS);
		this.code = code;
	}
}
