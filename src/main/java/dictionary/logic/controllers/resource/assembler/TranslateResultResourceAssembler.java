package dictionary.logic.controllers.resource.assembler;

import dictionary.logic.controllers.TranslateController;
import dictionary.model.dto.TranslateResultDto;
import dictionary.model.entity.PartOfSpeech;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Creates links for an object {@link TranslateResultDto}
 */
@Component
@RequiredArgsConstructor
public class TranslateResultResourceAssembler {
    private final WordResourceAssembler wordResourceAssembler;

    public TranslateResultDto setLinks(TranslateResultDto translateResult) {
        wordResourceAssembler.setLinks(translateResult.getOriginalWord());

        translateResult.getTranslateWordList().forEach(wordResourceAssembler::setLinks);

        translateResult.add(
                ControllerLinkBuilder.linkTo
                        (methodOn(TranslateController.class).getTranslate(
                                translateResult.getOriginalWord().getSpelling(),
                                translateResult.getOriginalWord().getLanguage().getCode(),
                                translateResult.getLanguageToTranslate().getCode()))
                        .withSelfRel());

        return translateResult;
    }
}
