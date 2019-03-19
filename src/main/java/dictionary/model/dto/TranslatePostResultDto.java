package dictionary.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.hateoas.ResourceSupport;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TranslatePostResultDto extends ResourceSupport {
    private String translateRelationUUID;
}
