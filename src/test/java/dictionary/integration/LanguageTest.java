package dictionary.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dictionary.logic.repository.LangRepository;
import dictionary.model.entity.Language;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class LanguageTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LangRepository langRepository;

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
    @DisplayName("Добавление языка")
    public void addLanguage() throws Exception {

        String response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/languages")
                        .headers(HTTP_HEADERS)
                        .content(objectMapper.writeValueAsString(
                                new Language().setCode("en").setInfo("English"))))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        Language language = objectMapper.readValue(response, Language.class);

        assertAll(
                "Проверка параметров языка",
                () -> Assertions.assertEquals("en", language.getCode()),
                () -> Assertions.assertEquals("English", language.getInfo())
        );
    }

    @Test
    @DisplayName("Изменение языка по коду")
    public void changeLanguage() throws Exception {

        String response = mvc.perform(
                MockMvcRequestBuilders
                        .put("/languages/en")
                        .headers(HTTP_HEADERS)
                        .content(objectMapper.writeValueAsString(
                                new Language().setInfo("English changed"))))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        Language language = objectMapper.readValue(response, Language.class);

        assertAll(
                "Проверка параметров языка",
                () -> Assertions.assertEquals("en", language.getCode()),
                () -> Assertions.assertEquals("English changed", language.getInfo())
        );
    }

    @Test
    @Sql("/sql/InsertLanguages.sql")
    @DisplayName("Получение всех языков")
    public void getLanguage() throws Exception {

        String response =
                mvc.perform(
                        MockMvcRequestBuilders
                                .get("/languages")
                                .headers(HTTP_HEADERS))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        JsonNode languageList = objectMapper.readTree(new StringReader(response)).get("_embedded").get("languageList");

        Set<Language> actualLanguages = objectMapper.readerFor(new TypeReference<Set<Language>>() {
        }).readValue(languageList);

        Set<Language> expectedLanguages = new HashSet<>(Arrays.asList(
                new Language().setCode("en").setInfo("English"),
                new Language().setCode("ru").setInfo("Русский"),
                new Language().setCode("by").setInfo("Беларускі")));

        Assertions.assertEquals(expectedLanguages, actualLanguages, "Checking the list of languages");
    }

    @Test
    @Sql("/sql/InsertLanguages.sql")
    @DisplayName("Получение языка по коду")
    public void getLanguageByCode() throws Exception {
        String response =
                mvc.perform(
                        MockMvcRequestBuilders
                                .get("/languages/en")
                                .headers(HTTP_HEADERS))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        Language actualLanguage = objectMapper.readValue(response, Language.class);

        Language expectedLanguage = new Language().setCode("en").setInfo("English");

        Assertions.assertEquals(expectedLanguage, actualLanguage, "Checking the list of languages");
    }

    @Test
    @Sql("/sql/InsertLanguages.sql")
    @DisplayName("Удаление языка")
    public void deleteLanguageByCode() throws Exception {

        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/languages/en")
                        .headers(HTTP_HEADERS))
                .andExpect(status().isNoContent());

        Set<Language> actualLanguages = new HashSet<>(langRepository.findAll());

        Set<Language> expectedLanguages = new HashSet<>(Arrays.asList(
                new Language().setCode("ru").setInfo("Русский"),
                new Language().setCode("by").setInfo("Беларускі")));

        Assertions.assertEquals(expectedLanguages, actualLanguages, "Checking the list of languages");
    }
}
