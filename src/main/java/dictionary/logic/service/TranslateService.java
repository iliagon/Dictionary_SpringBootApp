package dictionary.logic.service;

import dictionary.exception.DifferendPartOfSpeechException;
import dictionary.logic.repository.TranslateRepository;
import dictionary.model.entity.Language;
import dictionary.model.entity.TranslateRelation;
import dictionary.model.entity.TranslateRelationsPK;
import dictionary.model.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TranslateService {
    private final TranslateRepository repository;

    public List<TranslateRelation> findTranslate(Word word, Language language) {
        return repository.findTranslateRelations(word, language);
    }

    public List<TranslateRelation> findTranslate(String translateRelationsUUID) {
        return repository.findTranslateRelations(translateRelationsUUID);
    }

    public String addTranslate(Word word1, Word word2) {
        if (!word1.getPartOfSpeech().equals(word2.getPartOfSpeech()))
            throw new DifferendPartOfSpeechException();
//        if (word1.getLanguage().equals(word2.getLanguage()))
//            throw new SameLanguageException();

        String translateRelationUUID = chooseTranslateRelationId(word1, word2);
        repository.saveAll(Arrays.asList(
                new TranslateRelation().setCompositePK(new TranslateRelationsPK()
                        .setLang(word1.getLanguage())
                        .setWord(word1)
                        .setTranslateRelationUUID(translateRelationUUID)),
                new TranslateRelation().setCompositePK(new TranslateRelationsPK()
                        .setLang(word2.getLanguage())
                        .setWord(word2)
                        .setTranslateRelationUUID(translateRelationUUID))
        ));

        return translateRelationUUID;
    }

    /**
     * Choice translateRelationUUID
     */
    private String chooseTranslateRelationId(Word word1, Word word2) {
        List<TranslateRelation> translateRelations = repository.findTranslateRelations(word1, word2);
        String newTranslateRelationUUID;
        if (translateRelations.isEmpty())  //If there are no translations associated with the current words
            newTranslateRelationUUID = UUID.randomUUID().toString();
        else {
            newTranslateRelationUUID = translateRelations.get(0).getCompositePK().getTranslateRelationUUID(); //Get the TranslateRelationUUID of one of the words
            if (translateRelations.size() > 1) { //If another word has another TranslateRelationUUID, update it.
                String oldTranslateRelationUUID = translateRelations.get(1).getCompositePK().getTranslateRelationUUID();
                if (!oldTranslateRelationUUID.equals(newTranslateRelationUUID))
                    repository.updateTranslateRelationsUUID(oldTranslateRelationUUID, newTranslateRelationUUID);
            }
        }

        return newTranslateRelationUUID;
    }

    public void deleteTranslate(String translateRelationsUUID, Word word) {
        repository.deleteByTranslateRelationsUUIDAndWordId(translateRelationsUUID, word);
    }
}
