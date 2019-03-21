package dictionary.logic.controllers;

import dictionary.logic.controllers.resource.assembler.TranslateResultResourceAssembler;
import dictionary.logic.controllers.resource.assembler.WordResourceAssembler;
import dictionary.logic.service.LangService;
import dictionary.logic.service.WordService;
import dictionary.model.dto.*;
import dictionary.model.entity.Language;
import dictionary.model.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class TranslateController {
    private final WordService wordService;
    private final LangService langService;
    private final WordResourceAssembler assembler;

    private final TranslateResultResourceAssembler translateResultResourceAssembler;

    /**
     * Find translate and mapping result into {@link TranslateResultDto}
     */
    private TranslateResultDto findTranslate(Word word, Language languageToTranslate) {
        List<Word> translatedWords = wordService.findTranslatedWords(word, languageToTranslate);
        return new TranslateResultDto()
                .setOriginalWord(word)
                .setTranslateWordList(translatedWords)
                .setLanguageToTranslate(languageToTranslate);
    }

    @GetMapping("/translates")
    public Resources<TranslateResultDto> getTranslate(@RequestParam(value = "spelling") String spelling,
                                                      @RequestParam(value = "langFrom") String langFrom,
                                                      @RequestParam(value = "langTo") String langTo) {
        Language languageToTranslate = langService.findByCode(langTo);
        List<Word> wordToTranslateList = wordService.findWords(spelling, langService.findByCode(langFrom));

        List<TranslateResultDto> translateResultList = new ArrayList<>();
        wordToTranslateList.forEach(word ->
                translateResultList.add(findTranslate(word, languageToTranslate)));

        return new Resources<>(translateResultList,
                linkTo(methodOn(TranslateController.class).getTranslate(spelling, langFrom, langTo)).withSelfRel());
    }

    @GetMapping("/translates/{wordId}/{langTo}")
    public TranslateResultDto getTranslate(@PathVariable Long wordId,
                                           @PathVariable String langTo) {
        Language languageToTranslate = langService.findByCode(langTo);
        Word wordToTranslate = wordService.findById(wordId);
        TranslateResultDto translateResultDto = findTranslate(wordToTranslate, languageToTranslate);
        return translateResultResourceAssembler.setLinks(translateResultDto);
    }


    @PatchMapping("/translates")
    public ResponseEntity addTranslate(@Valid @RequestBody TranslatePostDto postTranslate) {
        wordService.addTranslate(
                wordService.findById(postTranslate.getWordId1()),
                wordService.findById(postTranslate.getWordId2()));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/translates/{wordId}")
    public ResponseEntity deleteWord(@PathVariable Long wordId) {
        wordService.deleteTranslateRelationFromWord(wordService.findById(wordId));
        return ResponseEntity.noContent().build();
    }
}
