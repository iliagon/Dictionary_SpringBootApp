package dictionary.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class TranslatePostDto {
    @NotNull
    private Long wordId1;
    @NotNull
    private Long wordId2;
}
