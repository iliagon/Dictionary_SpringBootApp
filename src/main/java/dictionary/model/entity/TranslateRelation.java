package dictionary.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@Entity
public class TranslateRelation implements Serializable {
    @EmbeddedId
    private TranslateRelationsPK compositePK;
}
