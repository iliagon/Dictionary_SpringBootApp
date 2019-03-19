package dictionary.model.dto;

import dictionary.model.entity.Word;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TranslateRelationDto {
    private String translateRelationUUID;
    private Word word;
}
