package dictionary.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Supper class for all 'BadRequestException'
 */
@Getter
public class BadRequestException extends ServiceException {
	private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
	private final String code;

	public BadRequestException(String message, String code) {
		super(message, code, HTTP_STATUS);
		this.code = code;
	}
}
