package dictionary.exception;

import dictionary.model.entity.Language;
import lombok.Getter;

@Getter
public class WordNotFoundException extends NotFoundException {
	private static final String ERROR_CODE = "WordNotFound";

	public WordNotFoundException(Long id) {
		super("Could not find word with wordId = " + id, ERROR_CODE);
	}

	public WordNotFoundException(String spelling, Language language) {
		super("Could not find word with spelling=" + spelling +"and language="+language, ERROR_CODE);
	}
}
