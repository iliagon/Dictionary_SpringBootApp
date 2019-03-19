package dictionary.exception;

import lombok.Getter;

@Getter
public class LangNotFoundException extends NotFoundException {
	private static final String ERROR_CODE = "LangNotFound";

	public LangNotFoundException(String code){
		super("Could not find language with code=" + code, ERROR_CODE);
	}
}
