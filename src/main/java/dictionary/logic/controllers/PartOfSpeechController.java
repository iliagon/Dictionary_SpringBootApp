package dictionary.logic.controllers;

import dictionary.logic.controllers.resource.assembler.PartOfSpeechAssembler;
import dictionary.logic.service.PartOfSpeechService;
import dictionary.model.entity.PartOfSpeech;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
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
public class PartOfSpeechController {

    private final PartOfSpeechAssembler assembler;
    private final PartOfSpeechService partOfSpeechService;

    // Aggregate root
    @GetMapping("/partOfSpeeches")
    public Resources<PartOfSpeech> getPartOfSpeechs() {
        List<PartOfSpeech> partOfSpeechList = partOfSpeechService.findAll();

        List<PartOfSpeech> lang = partOfSpeechList.stream()
                .map(assembler::setLinks)
                .collect(Collectors.toList());
        return new Resources<>(lang,
                linkTo(methodOn(PartOfSpeechController.class).getPartOfSpeechs()).withSelfRel());
    }

    @PostMapping("/partOfSpeeches")
    public ResponseEntity<PartOfSpeech> newPartOfSpeech(@Valid @RequestBody PartOfSpeech newPartOfSpeech) throws URISyntaxException {
        PartOfSpeech partOfSpeech = partOfSpeechService.add(newPartOfSpeech);

        PartOfSpeech resource = assembler.setLinks(partOfSpeech);
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Single item
    @GetMapping("/partOfSpeeches/{code}")
    public PartOfSpeech getPartOfSpeech(@PathVariable String code) {
        PartOfSpeech partOfSpeech = partOfSpeechService.findByCode(code);

        return assembler.setLinks(partOfSpeech);
    }

    @PutMapping("/partOfSpeeches/{code}")
    public ResponseEntity<PartOfSpeech> replacePartOfSpeech(@Valid @RequestBody PartOfSpeech partOfSpeech, @PathVariable String code) throws URISyntaxException {
        PartOfSpeech updatedLang = partOfSpeechService.replace(partOfSpeech, code);

        PartOfSpeech resource = assembler.setLinks(updatedLang);
        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    @DeleteMapping("/partOfSpeeches/{code}")
    public ResponseEntity<PartOfSpeech> deletePartOfSpeech(@PathVariable String code) {
        partOfSpeechService.delete(code);

        return ResponseEntity.noContent().build();
    }
}
