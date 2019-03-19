package dictionary.logic.controllers.resource.assembler;

import dictionary.logic.controllers.PartOfSpeechController;
import dictionary.model.entity.PartOfSpeech;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Creates links for an object {@link PartOfSpeech}
 */
@Component
public class PartOfSpeechAssembler{

	public PartOfSpeech setLinks(PartOfSpeech partOfSpeech) {
		partOfSpeech.add(ControllerLinkBuilder.linkTo(methodOn(PartOfSpeechController.class).getPartOfSpeech(partOfSpeech.getCode())).withSelfRel(),
				linkTo(methodOn(PartOfSpeechController.class).getPartOfSpeechs()).withRel("all"));

		return partOfSpeech;
	}
}
