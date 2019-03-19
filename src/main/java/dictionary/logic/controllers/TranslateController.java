package dictionary.logic.controllers;

import dictionary.logic.controllers.resource.assembler.TranslateResultResourceAssembler;
import dictionary.logic.service.LangService;
import dictionary.logic.service.TranslateService;
import dictionary.logic.service.WordService;
import dictionary.model.dto.*;
import dictionary.model.entity.Language;
import dictionary.model.entity.TranslateRelation;
import dictionary.model.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class TranslateController {
    private final WordService wordService;
    private final LangService langService;
    private final TranslateService translateService;
    private final TranslateResultResourceAssembler translateResultResourceAssembler;

    /**
     * Find translate and mapping result into {@link TranslateResultDto}
     */
    private TranslateResultDto findTranslate(Word word, Language languageToTranslate) {
        List<TranslateRelation> translate = translateService.findTranslate(word, languageToTranslate);

        List<Word> wordList = translate
                .stream().map(translateRelation -> translateRelation.getCompositePK().getWord())
                .collect(Collectors.toList());
        return new TranslateResultDto()
                .setTranslateRelationUUID(translate.get(0) == null ? null : translate.get(0).getCompositePK().getTranslateRelationUUID())
                .setOriginalWord(word)
                .setTranslateWordList(wordList)
                .setLanguageToTranslate(languageToTranslate);
    }

    @GetMapping("/translates")
    public Resources<TranslateResultDto> getTranslate(@RequestParam(value = "spelling") String spelling,
                                                      @RequestParam(value = "langFrom") String langFrom,
                                                      @RequestParam(value = "langTo") String langTo) {
        Language languageToTranslate = langService.findByCode(langTo);
        List<Word> wordToTranslateList = wordService.findBySpellingAndLang(spelling, langService.findByCode(langFrom));
        List<TranslateResultDto> translateResultList = new ArrayList<>();
        wordToTranslateList.forEach(word -> {
            TranslateResultDto translateResultDto = findTranslate(word, languageToTranslate);
            translateResultList.add(translateResultResourceAssembler.setLinks(translateResultDto));
        });

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
    public TranslatePostResultDto addTranslate(@Valid @RequestBody PostTranslateDto postTranslate) throws URISyntaxException {
        String translateRelationUUID = translateService.addTranslate(
                wordService.findById(postTranslate.getWordId1()),
                wordService.findById(postTranslate.getWordId2()));

        return new TranslatePostResultDto().setTranslateRelationUUID(translateRelationUUID);
    }

    @DeleteMapping("/translates")
    public ResponseEntity<Word> deleteWord(@RequestParam(value = "translateRelationUUID") String translateRelationsUUID, @RequestParam(value = "wordId") Long wordId) {
        translateService.deleteTranslate(translateRelationsUUID, wordService.findById(wordId));
        return ResponseEntity.noContent().build();
    }
}
