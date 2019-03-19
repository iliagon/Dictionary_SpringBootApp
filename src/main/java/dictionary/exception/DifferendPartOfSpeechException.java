package dictionary.exception;

import lombok.Getter;

@Getter
public class DifferendPartOfSpeechException extends BadRequestException {
	private static final String ERROR_CODE = "DifferendPartOfSpeech";

	public DifferendPartOfSpeechException() {
		super("Words have different parts of speech", ERROR_CODE);
	}
}
