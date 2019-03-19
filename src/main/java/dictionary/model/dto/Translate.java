package dictionary.model.dto;

import dictionary.model.entity.Word;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.hateoas.ResourceSupport;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Translate extends ResourceSupport {
    private String translateRelationUUID;
    private Word word1;
    private Word word2;
}
