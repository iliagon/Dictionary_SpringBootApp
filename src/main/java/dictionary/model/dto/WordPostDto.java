package dictionary.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class WordPostDto {
    @NotBlank
    private String spelling;
    @NotBlank
    private String language;
    @NotBlank
    private String partOfSpeech;
    private String meaning;
}
