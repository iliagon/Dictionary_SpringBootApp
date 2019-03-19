package dictionary.logic.controllers.resource.assembler;

import dictionary.logic.controllers.LanguageController;
import dictionary.model.entity.Language;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Creates links for an object {@link Language}
 */
@Component
public class LanguageAssembler {
	public Language setLinks(Language lang) {
		lang.add(
				ControllerLinkBuilder.linkTo(methodOn(LanguageController.class).getLanguages(lang.getCode())).withSelfRel(),
				linkTo(methodOn(LanguageController.class).getLanguages()).withRel("all"));
		return lang;
	}
}
