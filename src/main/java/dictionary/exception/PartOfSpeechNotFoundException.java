package dictionary.exception;

import lombok.Getter;

@Getter
public class PartOfSpeechNotFoundException extends NotFoundException {
	private static final String ERROR_CODE = "PartOfSpeechNotFound";

	public PartOfSpeechNotFoundException(String code) {
		super("Could not find part of speech with code=" + code, ERROR_CODE);
	}
}
