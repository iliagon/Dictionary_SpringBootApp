package dictionary.logic.controllers.resource.assembler;

import dictionary.logic.controllers.WordController;
import dictionary.model.entity.Language;
import dictionary.model.entity.PartOfSpeech;
import dictionary.model.entity.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Creates links for an object {@link Word}
 */
@Component
@RequiredArgsConstructor
public class WordResourceAssembler {
    private final LanguageAssembler languageAssembler;
    private final PartOfSpeechAssembler partOfSpeechAssembler;

    public Word setLinks(Word word) {
        Language language = word.getLanguage();
        language.removeLinks();
        languageAssembler.setLinks(language);

        PartOfSpeech partOfSpeech = word.getPartOfSpeech();
        partOfSpeech.removeLinks();
        partOfSpeechAssembler.setLinks(partOfSpeech);

        word.add(ControllerLinkBuilder.linkTo(methodOn(WordController.class).getWord(word.getWordId())).withSelfRel(),
                linkTo(methodOn(WordController.class).getWords("","")).withRel("all"));

        return word;
    }
}
