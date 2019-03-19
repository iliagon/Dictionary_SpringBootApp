package dictionary.logic.repository;

import dictionary.model.entity.Language;
import dictionary.model.entity.PartOfSpeech;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartOfSpeechRepository extends JpaRepository<PartOfSpeech, String> {
}
