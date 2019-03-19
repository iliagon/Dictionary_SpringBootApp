package dictionary.logic.service;

import dictionary.logic.repository.LangRepository;
import dictionary.logic.repository.PartOfSpeechRepository;
import dictionary.logic.repository.TranslateRepository;
import dictionary.logic.repository.WordRepository;
import dictionary.model.entity.TranslateRelation;
import dictionary.model.entity.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
class TranslateServiceTest {

    @Autowired
    TranslateRepository translateRepository;

    @Autowired
    LangRepository langRepository;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    PartOfSpeechRepository partOfSpeechRepository;

    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql",
            "/sql/logic/service/InsertPartOfTranslates.sql"
    })
    @DisplayName("Проверка что новому переводу присвоился тот же uuid что и остальным переводам в этой смысловой группе")
    void addTranslate() {
        TranslateService translateService = new TranslateService(translateRepository);
        Word word1 = wordRepository.getOne(1L);
        Word word3 =  wordRepository.getOne(3L);
        translateService.addTranslate(word1, word3);

        List<TranslateRelation> translateList = translateService.findTranslate(word3, langRepository.getOne("ru"));

        Assertions.assertFalse(translateList.isEmpty());
        String translateRelationUUID = translateList.get(0).getCompositePK().getTranslateRelationUUID();//remember any translateUUID

        //check that all translateUUID are the same
        Assertions.assertTrue(translateList.stream()
                .allMatch(translateRelation -> translateRelation.getCompositePK().getTranslateRelationUUID().equals(translateRelationUUID)));
    }
}