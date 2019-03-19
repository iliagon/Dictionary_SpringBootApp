package dictionary.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dictionary.logic.repository.LangRepository;
import dictionary.logic.repository.PartOfSpeechRepository;
import dictionary.logic.repository.WordRepository;
import dictionary.model.dto.WordPostDto;
import dictionary.model.entity.Language;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class WordTest {

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
            "/sql/InsertLanguages.sql"
    })
    @DisplayName("Добавление слова")
    public void addWord() throws Exception {

        String response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/words")
                        .headers(HTTP_HEADERS)
                        .content(objectMapper.writeValueAsString(
                                new WordPostDto().setLanguage("en").setPartOfSpeech("noun").setMeaning("meaning").setSpelling("spelling"))))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        Word word = objectMapper.readValue(response, Word.class);

        assertAll(
                "Проверка параметров слова",
                () -> Assertions.assertNotNull(word.getWordId()),
                () -> Assertions.assertEquals("meaning", word.getMeaning()),
                () -> Assertions.assertEquals("spelling", word.getSpelling()),
                () -> Assertions.assertEquals(partOfSpeechRepository.getOne("noun"), word.getPartOfSpeech()),
                () -> Assertions.assertEquals(langRepository.getOne("en"), word.getLanguage())
        );
    }

    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql"
    })
    @DisplayName("Изменение слова по коду")
    public void changeWord() throws Exception {

        String response = mvc.perform(
                MockMvcRequestBuilders
                        .put("/words/1")
                        .headers(HTTP_HEADERS)
                        .content(objectMapper.writeValueAsString(
                                new WordPostDto().setSpelling("changed").setMeaning("meaning changed").setPartOfSpeech("noun").setLanguage("ru"))))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        Word word = objectMapper.readValue(response, Word.class);

        assertAll(
                "Word Parameter Check",
                () -> Assertions.assertEquals(1L, word.getWordId().longValue()),
                () -> Assertions.assertEquals("meaning changed", word.getMeaning()),
                () -> Assertions.assertEquals("changed", word.getSpelling()),
                () -> Assertions.assertEquals(partOfSpeechRepository.getOne("noun"), word.getPartOfSpeech()),
                () -> Assertions.assertEquals(langRepository.getOne("ru"), word.getLanguage())
        );
    }

    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql"
    })
    @DisplayName("Получение всех слов")
    public void getWord() throws Exception {

        String response =
                mvc.perform(
                        MockMvcRequestBuilders
                                .get("/words")
                                .headers(HTTP_HEADERS))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        JsonNode wordList = objectMapper.readTree(new StringReader(response)).get("_embedded").get("wordList");

        Set<Word> actualWords = objectMapper.readerFor(new TypeReference<Set<Word>>() {
        }).readValue(wordList);

        Set<Word> expectedWords = new HashSet<>(Arrays.asList(
                new Word()
                        .setWordId(1L)
                        .setMeaning("Shed tears, especially as an expression of distress or pain")
                        .setSpelling("cry")
                        .setLanguage(langRepository.getOne("en"))
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb")),
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
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb")),
                new Word()
                        .setWordId(4L)
                        .setMeaning("Гучна казаць")
                        .setSpelling("крычаць")
                        .setLanguage(langRepository.getOne("by"))
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb"))));

        Assertions.assertEquals(expectedWords, actualWords, "Checking the list of words");
    }

    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql"
    })
    @DisplayName("Получение слова по id")
    public void getWordById() throws Exception {
        String response =
                mvc.perform(
                        MockMvcRequestBuilders
                                .get("/words/1")
                                .headers(HTTP_HEADERS))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        Word word = objectMapper.readValue(response, Word.class);

        assertAll(
                "Checking the list of languages",
                () -> Assertions.assertEquals(1L, word.getWordId().longValue()),
                () -> Assertions.assertEquals("Shed tears, especially as an expression of distress or pain", word.getMeaning()),
                () -> Assertions.assertEquals("cry", word.getSpelling()),
                () -> Assertions.assertEquals(partOfSpeechRepository.getOne("verb"), word.getPartOfSpeech()),
                () -> Assertions.assertEquals(langRepository.getOne("en"), word.getLanguage())
        );
    }

    @Test
    @Sql({
            "/sql/InsertPartOfSpeeches.sql",
            "/sql/InsertLanguages.sql",
            "/sql/InsertWords.sql"
    })
    @DisplayName("Удаление слова")
    public void deleteWordByCode() throws Exception {

        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/words/1")
                        .headers(HTTP_HEADERS))
                .andExpect(status().isNoContent());

        Set<Word> actualWords = new HashSet<>(wordRepository.findAll());

        Set<Word> expectedWords = new HashSet<>(Arrays.asList(
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
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb")),
                new Word()
                        .setWordId(4L)
                        .setMeaning("Гучна казаць")
                        .setSpelling("крычаць")
                        .setLanguage(langRepository.getOne("by"))
                        .setPartOfSpeech(partOfSpeechRepository.getOne("verb"))));

        Assertions.assertEquals(expectedWords, actualWords, "Checking the list of words");
    }
}
