package dictionary.model.dto;

import dictionary.model.entity.Language;
import dictionary.model.entity.Word;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TranslateResultDto extends ResourceSupport {
    private Word originalWord;
    private Language languageToTranslate;
    private List<Word> translateWordList;
}
