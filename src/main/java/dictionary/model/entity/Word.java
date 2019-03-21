package dictionary.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import java.io.Serializable;

//Совмещать Entity и DTO плохая практика. Но т.к. в данном случае они идентичны
//Entity и DTO объеденены в одну сущность
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
public class Word extends ResourceSupport implements Serializable {
    private @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "word_sq_generator")
    @SequenceGenerator(name="word_sq_generator", sequenceName = "WORD_SQ")
    Long wordId;

    @Column(nullable = false)
    private String spelling;

    @ManyToOne
    @JoinColumn(referencedColumnName = "code", nullable = false)
    private Language language;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "code", nullable = false)
    private PartOfSpeech partOfSpeech;

    private String meaning;

    /**
     * semantic identifier
     */
    @JsonIgnore
    private String translateUUID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        return wordId != null && wordId.equals(word.wordId);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
