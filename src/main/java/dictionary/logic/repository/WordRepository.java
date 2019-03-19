package dictionary.logic.repository;

import dictionary.model.entity.Language;
import dictionary.model.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByLanguage(Language language);

    List<Word> findBySpellingAndLanguage(String spelling, Language language);

    Optional<Word> findBySpellingAndLanguageAndMeaning(String spelling, Language language, String meaning);

    List<Word> findBySpelling(String spelling);
}
