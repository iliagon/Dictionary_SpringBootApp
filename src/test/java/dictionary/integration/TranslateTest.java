package dictionary.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dictionary.logic.repository.LangRepository;
import dictionary.logic.repository.PartOfSpeechRepository;
import dictionary.logic.repository.WordRepository;
import dictionary.model.dto.TranslatePostDto;
import dictionary.model.dto.TranslateResultDto;
import dictionary.model.entity.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class TranslateTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PartOfSpeechRepository partOfSpeechRepository;

    @Autowired
    private LangRepository langRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static HttpHeaders HTTP_HEADERS;

    @BeforeAll
    public static void setUpPrivateKey() {
        HTTP_HEADERS = new HttpHeaders();
        HTTP_HEADERS.add("Content-Type", "application/json");
        HTTP_HEADERS.add("Accept", "application/json");
    }

    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql"
    })
    @DisplayName("Добавление перевода")
    public void addTranslate() throws Exception {

        String response = mvc.perform(
                MockMvcRequestBuilders
                        .patch("/translates")
                        .headers(HTTP_HEADERS)
                        .content(objectMapper.writeValueAsString(
                                new TranslatePostDto().setWordId1(1L).setWordId2(2L))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }


    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql",
            "/sql/InsertTranslates.sql"
    })
    @DisplayName("Запрос перевода")
    public void getTranslateBySpellingAndLanguage() throws Exception {
        String spelling = "cry";
        String langFrom = "en";
        String langTo = "ru";
        String response =
                mvc.perform(
                        MockMvcRequestBuilders
                                .get("/translates?spelling={spelling}&langFrom={langFrom}&langTo={langTo}", spelling, langFrom, langTo)
                                .headers(HTTP_HEADERS))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        JsonNode translateResultListNode = objectMapper.readTree(new StringReader(response)).get("_embedded").get("translateResultDtoList");
        List<TranslateResultDto> translateResultList = objectMapper.readerFor(new TypeReference<List<TranslateResultDto>>() {
        }).readValue(translateResultListNode);
        TranslateResultDto translateResult = translateResultList.get(0);

        Set<Word> actualTranslateList = new HashSet<>(translateResult.getTranslateWordList());
        Set<Word> expectedTranslateList = new HashSet<>(Arrays.asList(
                new Word()
                        .setWordId(2L)
                        .setMeaning("Громко кричать, слишком громко разговаривать")
                        .setSpelling("орать")
                        .setLanguage(langRepository.getOne("ru"))
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb")),
                new Word()
                        .setWordId(3L)
                        .setMeaning("Громко говорить")
                        .setSpelling("Громко говорить")
                        .setLanguage(langRepository.getOne("ru"))
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb"))));

        assertAll(
                "Check Translation Parameters",
                () -> Assertions.assertEquals(spelling, translateResult.getOriginalWord().getSpelling()),
                () -> Assertions.assertEquals(langFrom, translateResult.getOriginalWord().getLanguage().getCode()),
                () -> Assertions.assertEquals(expectedTranslateList, actualTranslateList, "Check the list of word translations")
        );
    }

    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql",
            "/sql/InsertTranslates.sql"
    })
    @DisplayName("Запрос перевода")
    public void getTranslateByWordId() throws Exception {
        long wordId = 1L;
        String spelling = "cry";
        String langFrom = "en";
        String langTo = "ru";

        String response =
                mvc.perform(
                        MockMvcRequestBuilders
                                .get("/translates/{wordId}/{langTo}", wordId, langTo)
                                .headers(HTTP_HEADERS))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        TranslateResultDto translate = objectMapper.readValue(response, TranslateResultDto.class);

        Set<Word> actualTranslateList = new HashSet<>(translate.getTranslateWordList());
        Set<Word> expectedTranslateList = new HashSet<>(Arrays.asList(
                new Word()
                        .setWordId(2L)
                        .setMeaning("Громко кричать, слишком громко разговаривать")
                        .setSpelling("орать")
                        .setLanguage(langRepository.getOne("ru"))
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb")),
                new Word()
                        .setWordId(3L)
                        .setMeaning("Громко говорить")
                        .setSpelling("Громко говорить")
                        .setLanguage(langRepository.getOne("ru"))
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb"))));

        assertAll(
                "Проверка параметров перевода",
                () -> Assertions.assertEquals(spelling, translate.getOriginalWord().getSpelling()),
                () -> Assertions.assertEquals(langFrom, translate.getOriginalWord().getLanguage().getCode()),
                () -> Assertions.assertEquals(expectedTranslateList, actualTranslateList, "Check the list of word translations")
        );
    }

    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql",
            "/sql/InsertTranslates.sql"
    })
    @DisplayName("Удаление переводов")
    public void deleteTranslateByUUID() throws Exception {
        String translateUUID = "bf02d8a9-3287-49d6-bb7f-215fcf20693f";
        long wordId = 1L;

        //Проверка, что перевод, который удаляется в тесте существует
        Assertions.assertTrue(wordRepository.findAll().stream()
                .anyMatch(word ->
                        word.getWordId() == wordId &&
                                word.getTranslateUUID().equals(translateUUID)));

        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/translates/{wordId}", wordId)
                        .headers(HTTP_HEADERS))
                .andExpect(status().isNoContent());

        TestTransaction.end();

        //Проверка, что перевод удалился
        Assertions.assertFalse(wordRepository.findAll().stream()
                .anyMatch(word ->
                        word.getWordId() == wordId &&
                                word.getTranslateUUID().equals(translateUUID)));
    }
}
