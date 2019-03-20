package dictionary.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

//Совмещать Entity и DTO плохая практика. Но т.к. в данном случае они идентичны
//Entity и DTO объеденены в одну сущность
@Data
@EqualsAndHashCode(callSuper = false, exclude = "words")
@ToString(exclude = "words")
@Accessors(chain = true)
@Entity
public class PartOfSpeech extends ResourceSupport implements Serializable {
    /**
     * part of speech code (abbreviation)
     */
    private @Id String code;
    @NotBlank
    private String info;

    @JsonIgnore
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "partOfSpeech")
    private List<Word> words;
}
