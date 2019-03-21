package dictionary.logic.service;

import dictionary.exception.DifferendPartOfSpeechException;
import dictionary.exception.WordNotFoundException;
import dictionary.logic.repository.WordRepository;
import dictionary.model.entity.Language;
import dictionary.model.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WordService {
    private final WordRepository repository;

    public Word replaceWord(Word replaceWord, Long id) {
        return repository.save(replaceWord.setWordId(id));
    }

    public List<Word> findWords(String spelling, Language language) {
        return repository.findBySpellingAndLanguage(spelling, language);
    }

    public Optional<Word> findWords(String spelling, Language language, String meaning) {
        return repository.findBySpellingAndLanguageAndMeaning(spelling, language, meaning);
    }

    public List<Word> findWords(String spelling) {
        return repository.findBySpelling(spelling);
    }

    public Word findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new WordNotFoundException(id));
    }

    public List<Word> findWords(Language lang) {
        return repository.findByLanguage(lang);
    }

    public List<Word> findAll() {
        return repository.findAll();
    }

    /**
     * Добаление нового слова
     * <br>
     * Note: Если такое слово уже есть в базе - новое слово не создаётся
     */
    public Word add(Word newWord) {
        return findWords(newWord.getSpelling(), newWord.getLanguage(), newWord.getMeaning())
                .orElseGet(() -> repository.save(newWord));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Word> findTranslatedWords(Word word, Language language) {
        return repository.findTranslatedWords(word, language);
    }

    public String addTranslate(Word word1, Word word2) {
        if (!word1.getPartOfSpeech().equals(word2.getPartOfSpeech()))
            throw new DifferendPartOfSpeechException();

        String translateUUID = chooseTranslateRelationId(word1, word2);
        word1.setTranslateUUID(translateUUID);
        word2.setTranslateUUID((translateUUID));

        return translateUUID;
    }

    /**
     * Choice translateUUID
     */
    private String chooseTranslateRelationId(Word word1, Word word2) {
        String newTranslateUUID;
        //If there are no translations associated with the current words
        if (word1.getTranslateUUID() == null && word2.getTranslateUUID() == null)
            newTranslateUUID = UUID.randomUUID().toString();
        else {
            String anotherTranslateUUID;
            //Get the TranslateRelationUUID of one of the words
            if (word1.getTranslateUUID() != null) {
                newTranslateUUID = word1.getTranslateUUID();
                anotherTranslateUUID = word2.getTranslateUUID();
            } else {
                newTranslateUUID = word2.getTranslateUUID();
                anotherTranslateUUID = word1.getTranslateUUID();
            }
            //If another word has another TranslateRelationUUID, update it.
            if (!newTranslateUUID.equals(anotherTranslateUUID)) {
                    repository.updateTranslateRelationsUUID(anotherTranslateUUID, newTranslateUUID);
            }
        }
        return newTranslateUUID;
}

    public void deleteTranslateRelationFromWord(Word word) {
        repository.deleteTranslateRelationFromWord(word);
    }
}
