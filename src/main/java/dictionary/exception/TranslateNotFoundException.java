package dictionary.exception;

import lombok.Getter;

@Getter
public class TranslateNotFoundException extends NotFoundException {
	private static final String ERROR_CODE = "LangNotFound";

	public TranslateNotFoundException(String uuid){
		super("Could not find translate with translateRelationUUID=" + uuid, ERROR_CODE);
	}
}
