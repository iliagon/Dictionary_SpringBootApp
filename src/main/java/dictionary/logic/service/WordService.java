package dictionary.logic.service;

import dictionary.exception.WordNotFoundException;
import dictionary.logic.repository.WordRepository;
import dictionary.model.entity.Language;
import dictionary.model.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {
    private final WordRepository repository;
    private final TranslateService translateService;

    public Word replaceWord(Word replaceWord, Long id) {

        return repository.findById(id)
                .map(word -> word
                        .setSpelling(replaceWord.getSpelling())
                        .setLanguage(replaceWord.getLanguage())
                        .setPartOfSpeech(replaceWord.getPartOfSpeech())
                        .setMeaning(replaceWord.getMeaning())
                )
                .orElseGet(() ->
                        repository.save(replaceWord.setWordId(id))
                );
    }

    public List<Word> findBySpellingAndLang(String spelling, Language language) {
        return repository.findBySpellingAndLanguage(spelling, language);
    }

    public Optional<Word> findBySpellingAndLangAndMeaning(String spelling, Language language, String meaning) {
        return repository.findBySpellingAndLanguageAndMeaning(spelling, language, meaning);
    }

    public List<Word> findBySpelling(String spelling) {
        return repository.findBySpelling(spelling);
    }

    public Word findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new WordNotFoundException(id));
    }

    public List<Word> findWordsByLanguage(Language lang) {
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
        return findBySpellingAndLangAndMeaning(newWord.getSpelling(), newWord.getLanguage(), newWord.getMeaning())
                .orElseGet(() -> repository.save(newWord));
    }

    public void delete(Long id) {
//        translateService.findTranslateRelations(findById(id))
//                .forEach(translateRelation ->
//                        translateService.deleteTranslate(translateRelation.getCompositePK().getTranslateRelationUUID(),translateRelation.getCompositePK().getWord()));
        repository.deleteById(id);
    }
}
