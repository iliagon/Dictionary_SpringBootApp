package dictionary.logic.controllers;

import dictionary.logic.controllers.resource.assembler.LanguageAssembler;
import dictionary.logic.service.LangService;
import dictionary.model.entity.Language;
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
public class LanguageController {

    private final LanguageAssembler assembler;
    private final LangService langService;

    // Aggregate root
    @GetMapping("/languages")
    public Resources<Language> getLanguages() {
        List<Language> languageList = langService.findAll();
        
        List<Language> lang = languageList.stream()
                .map(assembler::setLinks)
                .collect(Collectors.toList());
        return new Resources<>(lang,
                linkTo(methodOn(LanguageController.class).getLanguages()).withSelfRel());
    }

    @PostMapping("/languages")
    public ResponseEntity<Language> newLanguages(@Valid @RequestBody Language newLang) throws URISyntaxException {
        Language language = langService.add(newLang);

        Language resource = assembler.setLinks(language);
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Single item
    @GetMapping("/languages/{code}")
    public Language getLanguages(@PathVariable String code) {
        Language language = langService.findByCode(code);

        return assembler.setLinks(language);
    }

    @PutMapping("/languages/{code}")
    public ResponseEntity<Language> replaceLanguages(@Valid @RequestBody Language language, @PathVariable String code) throws URISyntaxException {
        Language updatedLang = langService.replace(language, code);

        Language resource = assembler.setLinks(updatedLang);
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/languages/{code}")
    public ResponseEntity<Language> deleteLanguages(@PathVariable String code) {
        langService.delete(code);

        return ResponseEntity.noContent().build();
    }
}
