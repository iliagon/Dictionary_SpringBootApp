package dictionary.logic.repository;

import dictionary.model.entity.Language;
import dictionary.model.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByLanguage(Language language);

    List<Word> findBySpellingAndLanguage(String spelling, Language language);

    Optional<Word> findBySpellingAndLanguageAndMeaning(String spelling, Language language, String meaning);

    List<Word> findBySpelling(String spelling);

    @Query("select w from Word w " +
            "where w.language = :language and w.translateUUID in " +
            "                         (select w1.translateUUID from Word w1 " +
            "                           where w1 = :word1)")
    List<Word> findTranslatedWords(Word word1, Language language);

    @Modifying
    @Transactional
    @Query("update Word w set w.translateUUID = null " +
            "where w = :word")
    void deleteTranslateRelationFromWord(Word word);

    @Modifying
    @Transactional
    @Query("update Word w set w.translateUUID = :newTranslateUUID " +
            "where w.translateUUID = :oldTranslateUUID")
    void updateTranslateRelationsUUID(String oldTranslateUUID, String newTranslateUUID);
}
