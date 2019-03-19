package dictionary.exception;

import lombok.Getter;

@Getter
public class SameLanguageException extends BadRequestException {
	private static final String ERROR_CODE = "SameLanguage";

	public SameLanguageException() {
		super("Words have the same language", ERROR_CODE);
	}
}
