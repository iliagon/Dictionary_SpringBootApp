package dictionary.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@Embeddable
public class TranslateRelationsPK implements Serializable {
    @Column(nullable = false)
    private String translateRelationUUID;

    @ManyToOne
    @JoinColumn(referencedColumnName = "code", nullable = false)
    private Language lang;

    @ManyToOne
    @JoinColumn(referencedColumnName = "wordId", nullable = false)
    private Word word;
}
