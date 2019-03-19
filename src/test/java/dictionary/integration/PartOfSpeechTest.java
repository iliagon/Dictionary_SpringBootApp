package dictionary.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dictionary.logic.repository.PartOfSpeechRepository;
import dictionary.model.entity.PartOfSpeech;
import dictionary.model.entity.PartOfSpeech;
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
public class PartOfSpeechTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PartOfSpeechRepository partOfSpeechRepository;

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
    @DisplayName("Добавление части речи")
    public void addPartOfSpeech() throws Exception {

        String response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/partOfSpeeches")
                        .headers(HTTP_HEADERS)
                        .content(objectMapper.writeValueAsString(
                                new PartOfSpeech().setCode("noun").setInfo("Naming word"))))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        PartOfSpeech partOfSpeech = objectMapper.readValue(response, PartOfSpeech.class);

        assertAll(
                "Verification of the part of speech",
                () -> Assertions.assertEquals("noun", partOfSpeech.getCode()),
                () -> Assertions.assertEquals("Naming word", partOfSpeech.getInfo())
        );
    }

    @Test
    @Sql("/sql/InsertPartOfSpeeches.sql")
    @DisplayName("Изменение части речи по коду")
    public void changePartOfSpeech() throws Exception {

        String response = mvc.perform(
                MockMvcRequestBuilders
                        .put("/partOfSpeeches/noun")
                        .headers(HTTP_HEADERS)
                        .content(objectMapper.writeValueAsString(
                                new PartOfSpeech().setInfo("Naming word changed"))))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        PartOfSpeech partOfSpeech = objectMapper.readValue(response, PartOfSpeech.class);

        assertAll(
                "Verification of the part of speech",
                () -> Assertions.assertEquals("noun", partOfSpeech.getCode()),
                () -> Assertions.assertEquals("Naming word changed", partOfSpeech.getInfo())
        );
    }

    @Test
    @Sql("/sql/InsertPartOfSpeeches.sql")
    @DisplayName("Получение всех частей речи")
    public void getPartOfSpeech() throws Exception {

        String response =
                mvc.perform(
                        MockMvcRequestBuilders
                                .get("/partOfSpeeches")
                                .headers(HTTP_HEADERS))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        JsonNode partOfSpeechesList = objectMapper.readTree(new StringReader(response)).get("_embedded").get("partOfSpeechList");

        Set<PartOfSpeech> actualPartOfSpeeches = objectMapper.readerFor(new TypeReference<Set<PartOfSpeech>>(){}).readValue(partOfSpeechesList);

        Set<PartOfSpeech> expectedPartOfSpeeches = new HashSet<>(Arrays.asList(
                new PartOfSpeech().setCode("noun").setInfo("Naming word"),
                new PartOfSpeech().setCode("verb").setInfo("Action Word"),
                new PartOfSpeech().setCode("interjection").setInfo("Expressive word")));

        Assertions.assertEquals(expectedPartOfSpeeches, actualPartOfSpeeches);
    }

    @Test
    @Sql("/sql/InsertPartOfSpeeches.sql")
    @DisplayName("Получение части речи по коду")
    public void getPartOfSpeechByCode() throws Exception {
        String response =
                mvc.perform(
                        MockMvcRequestBuilders
                                .get("/partOfSpeeches/noun")
                                .headers(HTTP_HEADERS))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        PartOfSpeech actualPartOfSpeech = objectMapper.readValue(response, PartOfSpeech.class);

        PartOfSpeech expectedPartOfSpeech = new PartOfSpeech().setCode("noun").setInfo("Naming word");

        Assertions.assertEquals(expectedPartOfSpeech, actualPartOfSpeech);
    }

    @Test
    @Sql("/sql/InsertPartOfSpeeches.sql")
    @DisplayName("Удаление части речи")
    public void deletePartOfSpeechByCode() throws Exception {

        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/partOfSpeeches/noun")
                        .headers(HTTP_HEADERS))
                .andExpect(status().isNoContent());

        Set<PartOfSpeech> actualPartOfSpeechs = new HashSet<>(partOfSpeechRepository.findAll());

        Set<PartOfSpeech> expectedPartOfSpeechs = new HashSet<>(Arrays.asList(
                new PartOfSpeech().setCode("verb").setInfo("Action Word"),
                new PartOfSpeech().setCode("interjection").setInfo("Expressive word")));

        Assertions.assertEquals(expectedPartOfSpeechs, actualPartOfSpeechs);
    }
}
