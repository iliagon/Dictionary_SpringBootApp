package dictionary.logic.controllers;

import dictionary.logic.controllers.resource.assembler.WordResourceAssembler;
import dictionary.logic.service.LangService;
import dictionary.logic.service.PartOfSpeechService;
import dictionary.logic.service.WordService;
import dictionary.model.dto.WordPostDto;
import dictionary.model.entity.Language;
import dictionary.model.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class WordController {

    private final WordResourceAssembler assembler;
    private final WordService wordService;
    private final LangService langService;
    private final PartOfSpeechService partOfSpeechService;

    // Aggregate root
    @GetMapping("/words")
    public Resources<Word> getWords(@RequestParam(value = "spelling", required = false) String spelling,
                                    @RequestParam(value = "language", required = false) String langCode) {
        List<Word> wordEntities;
        if (langCode != null && !langCode.isEmpty() && spelling != null && !spelling.isEmpty()) {
            Language language = langService.findByCode(langCode);
            wordEntities = wordService.findBySpellingAndLang(spelling, language);
        } else if (langCode != null && !langCode.isEmpty()) {
            Language language = langService.findByCode(langCode);
            wordEntities = wordService.findWordsByLanguage(language);
        } else if (spelling != null && !spelling.isEmpty())
            wordEntities = wordService.findBySpelling(spelling);
        else
            wordEntities = wordService.findAll();

        List<Word> words = wordEntities.stream()
                .map(assembler::setLinks)
                .collect(Collectors.toList());
        return new Resources<>(words,
                linkTo(methodOn(WordController.class).getWords("", "")).withSelfRel());
    }

    @PostMapping("/words")
    public ResponseEntity<Word> newWord(@Valid @RequestBody WordPostDto newWord) throws URISyntaxException {
        Word wordEntity = new Word()
                .setSpelling(newWord.getSpelling())
                .setLanguage(langService.findByCode(newWord.getLanguage()))
                .setPartOfSpeech(partOfSpeechService.findByCode(newWord.getPartOfSpeech()))
                .setMeaning(newWord.getMeaning());
        Word savedWord = wordService.add(wordEntity);

        Word resource = assembler.setLinks(savedWord);
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Single item
    @GetMapping("/words/{id}")
    public Word getWord(@PathVariable Long id) {
        Word word = wordService.findById(id);

        return assembler.setLinks(word);
    }

    @PutMapping("/words/{id}")
    public ResponseEntity<Word> replaceWord(@Valid @RequestBody WordPostDto replaceWord, @PathVariable Long id) throws URISyntaxException {
        Word wordEntity = new Word()
                .setSpelling(replaceWord.getSpelling())
                .setLanguage(langService.findByCode(replaceWord.getLanguage()))
                .setPartOfSpeech(partOfSpeechService.findByCode(replaceWord.getPartOfSpeech()))
                .setMeaning(replaceWord.getMeaning());
        Word updatedWord = wordService.replaceWord(wordEntity, id);

        Word resource = assembler.setLinks(updatedWord);
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/words/{id}")
    public ResponseEntity<Word> deleteWord(@PathVariable Long id) {
        wordService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
